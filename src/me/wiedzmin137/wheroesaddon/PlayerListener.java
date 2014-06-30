package me.wiedzmin137.wheroesaddon;

import me.wiedzmin137.wheroesaddon.util.Properties;
import me.wiedzmin137.wheroesaddon.util.SkillPointChangeEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.herocraftonline.heroes.api.events.ClassChangeEvent;
import com.herocraftonline.heroes.api.events.HeroChangeLevelEvent;
import com.herocraftonline.heroes.api.events.SkillDamageEvent;
import com.herocraftonline.heroes.api.events.SkillUseEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.effects.Effect;
import com.herocraftonline.heroes.characters.skill.Skill;

public class PlayerListener implements Listener {
	private WHeroesAddon p;
	
	public PlayerListener(WHeroesAddon plugin) {
		this.p = plugin;
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				p.getDatabaseManager().loadPlayer(event.getPlayer());
				
				final Hero hero = WHeroesAddon.heroes.getCharacterManager().getHero(event.getPlayer());
				Bukkit.getScheduler().scheduleSyncDelayedTask(p, new Runnable() {
					public void run() {
						for (Effect effect : hero.getEffects()) {
							Skill skill = WHeroesAddon.heroes.getSkillManager().getSkill(effect.getName());
							if (skill != null) {
								if (WHeroesAddon.getInstance().getPlayerData(event.getPlayer()).isLocked(skill))
									hero.removeEffect(effect);
							}
						}
					}
				 }, 1L);
			}
 
		}.runTaskLater(p, 5);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerLeave(PlayerQuitEvent event) {
		p.getDatabaseManager().savePlayer(p.getPlayerData(event.getPlayer()));
	}
	
	@EventHandler
	public void onLevelChange(HeroChangeLevelEvent event) {
		final Hero hero = event.getHero();
		PlayerData pd = p.getPlayerData(event.getHero().getPlayer());
		int amount = (event.getTo() - event.getFrom()) * (int)Properties.SKILLTREE_POINTS_PER_LEVEL.getValue();
		pd.setPoints(amount + pd.getPoints());
		if (hero.getHeroClass() != event.getHeroClass()) {
			return;
		}
		new SkillPointChangeEvent(hero.getPlayer(), hero.getHeroClass(), amount - pd.getPoints());
	}
	
	@EventHandler
	public void onPointGain(SkillPointChangeEvent event) {
		p.getDatabaseManager().savePlayer(p.getPlayerData(event.getPlayer()));
	}
	
	@EventHandler
	public void onClassChangeEvent(ClassChangeEvent event) {
		//TODO change players Skill Data
	}
	
	@EventHandler
	public void onSkillUse(SkillUseEvent event) {
		//TODO change Skill usage properties by SkillPoints in SkillTree
	}
	
	@EventHandler
	public void onSkillDamage(SkillDamageEvent event) {
		//TODO change skill damages by SkillPoints in SkillTree
	}
}

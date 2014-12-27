package me.wiedzmin137.wheroesaddon;

import me.wiedzmin137.wheroesaddon.data.Lang;
import me.wiedzmin137.wheroesaddon.data.PlayerData;
import me.wiedzmin137.wheroesaddon.data.Properties;
import me.wiedzmin137.wheroesaddon.util.SkillPointChangeEvent;
import me.wiedzmin137.wheroesaddon.util.SkillUnlockEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.herocraftonline.heroes.api.events.ClassChangeEvent;
import com.herocraftonline.heroes.api.events.HeroChangeLevelEvent;
import com.herocraftonline.heroes.api.events.SkillDamageEvent;
import com.herocraftonline.heroes.api.events.SkillUseEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import com.herocraftonline.heroes.characters.effects.Effect;
import com.herocraftonline.heroes.characters.skill.Skill;
import com.herocraftonline.heroes.characters.skill.SkillConfigManager;

public class PlayerListener implements Listener {
	private WHeroesAddon p;
	
	public PlayerListener(WHeroesAddon plugin) {
		this.p = plugin;
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		p.getDatabaseManager().loadPlayer(event.getPlayer());
				
		final Hero hero = WHeroesAddon.heroes.getCharacterManager().getHero(event.getPlayer());
		Bukkit.getScheduler().scheduleSyncDelayedTask(p, new Runnable() {
			@Override
			public void run() {
				for (Effect effect : hero.getEffects()) {
					Skill skill = WHeroesAddon.heroes.getSkillManager().getSkill(effect.getName());
					if (skill != null) {
						if (WHeroesAddon.getInstance().getPlayerData(event.getPlayer()).isLocked(skill))
							hero.removeEffect(effect);
					}
				}
				if (hero.getHeroClass().isDefault()) { //TODO add Properties value
					p.getChooseMenu().open(hero.getPlayer());
				}
			}
		}, 1L);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerLeave(PlayerQuitEvent event) {
		p.getDatabaseManager().savePlayer(p.getPlayerData(event.getPlayer()));
		p.setPlayerData(event.getPlayer(), null);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onLevelChange(HeroChangeLevelEvent event) {
		final Hero hero = event.getHero();
		PlayerData pd = p.getPlayerData(event.getHero().getPlayer());
		int amount = (event.getTo() - event.getFrom()) * (int) Properties.SKILLTREE_POINTS_PER_LEVEL.getValue();
		pd.setPoints(amount + pd.getPoints());
		pd.recountLock();
		if (hero.getHeroClass() != event.getHeroClass()) {
			return;
		}
		new SkillPointChangeEvent(hero.getPlayer(), hero.getHeroClass(), amount - pd.getPoints());
	}
	
	@EventHandler
	public void onSkillUnlock(SkillUnlockEvent event) {
		p.getPlayerData(event.getPlayer()).setLocked(event.getSkill(), false);
		p.getDatabaseManager().savePlayer(p.getPlayerData(event.getPlayer()));
	}
	
	@EventHandler
	public void onPointGain(SkillPointChangeEvent event) {
		p.getDatabaseManager().savePlayer(p.getPlayerData(event.getPlayer()));
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onClassChangeEvent(final ClassChangeEvent event) {
		//TODO test old error on /Hero reset, it shouldn't exist
		Bukkit.getScheduler().scheduleSyncDelayedTask(p, new Runnable() {
			@Override
			public void run() {
				final PlayerData pd = p.getPlayerData(event.getHero().getPlayer());
				final Hero hero = event.getHero();
				boolean reset = false;
				if (event.getTo().isDefault()) {
					reset = true;
					for (HeroClass hClass : WHeroesAddon.heroes.getClassManager().getClasses()) {
						if (hero.getExperience(hClass) != 0.0D) {
							reset = false;
							break;
						}
					}
				}
				if (reset) {
					pd.reset();
					p.getDatabaseManager().savePlayer(pd);
				} else {
					pd.countPlayerPoints();
				}
				pd.recountLock();
				for (Effect effect : hero.getEffects()) {
					Skill skill = WHeroesAddon.heroes.getSkillManager().getSkill(effect.getName());
					if (skill != null) {
						if (pd.isLocked(skill))
							hero.removeEffect(effect);
					}
				}
				
//				for (String skill : hero.getHeroClass().getSkillNames())
//					pd.actualizeMenuVariables(WHeroesAddon.heroes.getSkillManager().getSkill(skill));
				p.getDatabaseManager().savePlayer(p.getPlayerData(pd.getPlayer()));
			}
		}, 1L);
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onSkillUse(SkillUseEvent event) {
		PlayerData pd = p.getPlayerData(event.getHero().getPlayer());
		Hero hero = event.getHero();
		Skill skill = event.getSkill();
		if (pd.isLocked(event.getSkill()) && !event.getPlayer().hasPermission("skilltree.override.locked")) {
			event.getPlayer().sendMessage(Lang.SKILLTREE_ERROR_NO_ACCESS.toString());
			//TODO check: event.getHero().hasEffect(event.getSkill().getName());
			event.setCancelled(true);
			return;
		}

		int health = (int) SkillConfigManager.getUseSetting(hero, skill, "st-health", 0.0D, false)
				* pd.getSkillLevel(skill);
		health = (health > 0) ? health : 0;
		event.setHealthCost(event.getHealthCost() + health);
			    
		int mana = (int) SkillConfigManager.getUseSetting(hero, skill, "st-mana", 0.0D, false)
				* pd.getSkillLevel(skill);
		mana = (mana > 0) ? mana : 0;
		event.setManaCost(event.getManaCost() - mana);

		int reagent = (int) SkillConfigManager.getUseSetting(hero, skill, "st-reagent", 0.0D, false)
				* pd.getSkillLevel(skill);
		reagent = (reagent > 0) ? reagent : 0;
		ItemStack is = event.getReagentCost();
		if (is != null) { is.setAmount(event.getReagentCost().getAmount() - reagent); }
		event.setReagentCost(is);

		int stamina = (int) SkillConfigManager.getUseSetting(hero, skill, "st-stamina", 0.0D, false)
				* pd.getSkillLevel(skill);
		stamina = (stamina > 0) ? stamina : 0;
		event.setStaminaCost(event.getStaminaCost() - stamina);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onSpellDamageEvent(SkillDamageEvent event) {
		if (event.getDamager() instanceof Hero) {
			int modifieddmg = (int) SkillConfigManager.getUseSetting((Hero) event.getDamager(), event.getSkill(), "st-damage", 0.0D, false)
					* (p.getPlayerData(((Hero) event.getDamager()).getPlayer())).getSkillLevel(event.getSkill());
			event.setDamage(event.getDamage() + modifieddmg);
		}
	}
}

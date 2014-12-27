package me.wiedzmin137.wheroesaddon.util;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;

public class FirstClassChooseEvent extends Event {
    private static final HandlerList handlers;
    protected final Hero hero;
    protected HeroClass to;
    
    public FirstClassChooseEvent(final Hero hero, final HeroClass to) {
        super();
        this.hero = hero;
        this.to = to;
    }
    
    public final Hero getHero() {
        return this.hero;
    }
    
    public HeroClass getTo() {
        return this.to;
    }
    
    @Override
	public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}

package me.wiedzmin137.wheroesaddon.commands;

import org.bukkit.entity.Player;

import me.desht.scrollingmenusign.SMSException;
import me.desht.scrollingmenusign.SMSHandler;
import me.desht.scrollingmenusign.SMSMenu;
import me.desht.scrollingmenusign.enums.SMSMenuAction;
import me.desht.scrollingmenusign.views.SMSInventoryView;
import me.wiedzmin137.wheroesaddon.WHeroesAddon;
import me.wiedzmin137.wheroesaddon.util.Lang;

public class ChooseCommand {
	private static SMSHandler smsHandler;
	public static void showClassChoose(Player player) {
		SMSMenu menuChoose = null;
		
		if (smsHandler == null) { return; }
		try { menuChoose = smsHandler.getMenu("ClassChoose"); }
		catch (SMSException e) {
			menuChoose = smsHandler.createMenu("ClassChoose", Lang.GUI_TITLE_CHOOSE.toString(), WHeroesAddon.getInstance());
		}
		
		SMSInventoryView view = null;
		try {
			view = (SMSInventoryView)smsHandler.getViewManager().getView("ClassChoose");
		} catch (SMSException e) {
			view = new SMSInventoryView("ClassChoose", menuChoose);
			view.update(menuChoose, SMSMenuAction.REPAINT);
			smsHandler.getViewManager().registerView(view);
		}
		view.toggleGUI(player);
	}
}

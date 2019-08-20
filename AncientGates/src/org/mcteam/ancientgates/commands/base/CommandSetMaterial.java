package org.mcteam.ancientgates.commands.base;

import java.util.Arrays;

import org.mcteam.ancientgates.Conf;
import org.mcteam.ancientgates.Gate;
import org.mcteam.ancientgates.Gates;
import org.mcteam.ancientgates.commands.BaseCommand;
import org.mcteam.ancientgates.util.TextUtil;
import org.mcteam.ancientgates.util.types.GateMaterial;

public class CommandSetMaterial extends BaseCommand {

	public CommandSetMaterial() {
		aliases.add("setmaterial");

		requiredParameters.add("id");
		requiredParameters.add("material");

		requiredPermission = "ancientgates.setmaterial";

		senderMustBePlayer = false;

		helpDescription = "Set portal \"material\" for gate";
	}

	@Override
	public void perform() {
		final String material = parameters.get(1).toUpperCase();

		// Whether the given gate material is valid depends on whether only
		// vanilla portal blocks are allowed:
		final GateMaterial materialFromName = GateMaterial.fromName(material);
		if (materialFromName == null || (Conf.useVanillaPortals && !materialFromName.isVanilla())) {
			sendMessage("This is not a valid gate material. Valid materials:");
			sendMessage(TextUtil.implode(Arrays.asList(Conf.useVanillaPortals ? GateMaterial.vanillaNames : GateMaterial.names), Conf.colorSystem + ", "));
			return;
		}

		final boolean isOpen = Gates.isOpen(gate);

		if (isOpen)
			Gates.close(gate);
		gate.setMaterial(material);
		if (isOpen)
			Gates.open(gate);
		sendMessage("Portal material for gate \"" + gate.getId() + "\" is now " + material + ".");

		Gate.save();
	}

}

package net.coderbot.iris.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.coderbot.iris.Iris;
import net.coderbot.iris.gui.element.ShaderPackListWidget;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
//import net.minecraft.client.gui.screen.ScreenTexts;
//import net.minecraft.client.gui.widget.ButtonWidget;
//import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.text.Text;
//import net.minecraft.text.TranslatableText;
//import net.minecraft.util.Formatting;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ShaderPackScreen extends Screen implements HudHideable {
	private static final ITextComponent SELECT_TITLE = new TranslationTextComponent("pack.iris.select.title").mergeStyle(TextFormatting.GRAY, TextFormatting.ITALIC);

	private final Screen parent;

	private ShaderPackListWidget shaderPackList;

	private ITextComponent addedPackDialog = null;
	private int addedPackDialogTimer = 0;

	private boolean dropChanges = false;

	public ShaderPackScreen(Screen parent) {
		super(new TranslationTextComponent("options.iris.shaderPackSelection.title"));

		this.parent = parent;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (this.minecraft.world == null) {
			this.renderBackground(matrices);
		} else {
			this.fillGradient(matrices, 0, 0, width, height, 0x4F232323, 0x4F232323);
		}

		this.shaderPackList.render(matrices, mouseX, mouseY, delta);

		drawCenteredString(matrices, this.font, this.title, (int)(this.width * 0.5), 8, 0xFFFFFF);

		if (addedPackDialog != null && addedPackDialogTimer > 0) {
			drawCenteredString(matrices, this.font, addedPackDialog, (int)(this.width * 0.5), 21, 0xFFFFFF);
		} else {
			drawCenteredString(matrices, this.font, SELECT_TITLE, (int)(this.width * 0.5), 21, 0xFFFFFF);
		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	protected void init() {
		super.init();
		int bottomCenter = this.width / 2 - 50;
		int topCenter = this.width / 2 - 76;
		boolean inWorld = this.minecraft.world != null;

		this.shaderPackList = new ShaderPackListWidget(this.minecraft, this.width, this.height, 32, this.height - 58, 0, this.width);

		if (inWorld) {
			this.shaderPackList.func_244605_b(false);
		}

		this.children.add(shaderPackList);

		this.addButton(new Button(bottomCenter + 104, this.height - 27, 100, 20,
			DialogTexts.GUI_DONE, button -> onClose()));

		this.addButton(new Button(bottomCenter, this.height - 27, 100, 20,
			new TranslationTextComponent("options.iris.apply"), button -> this.applyChanges()));

		this.addButton(new Button(bottomCenter - 104, this.height - 27, 100, 20,
			DialogTexts.GUI_CANCEL, button -> this.dropChangesAndClose()));

		this.addButton(new Button(topCenter - 78, this.height - 51, 152, 20,
			new TranslationTextComponent("options.iris.openShaderPackFolder"), button -> openShaderPackFolder()));

		this.addButton(new Button(topCenter + 78, this.height - 51, 152, 20,
			new TranslationTextComponent("options.iris.refreshShaderPacks"), button -> this.shaderPackList.refresh()));
	}

	@Override
	public void tick() {
		super.tick();

		if (this.addedPackDialogTimer > 0) {
			this.addedPackDialogTimer--;
		}
	}

	@Override
	public void addPacks(List<Path> paths) {
		List<Path> packs = paths.stream().filter(Iris::isValidShaderpack).collect(Collectors.toList());

		for (Path pack : packs) {
			String fileName = pack.getFileName().toString();

			try {
				copyShaderPack(pack, fileName);
			} catch (FileAlreadyExistsException e) {
				this.addedPackDialog = new TranslationTextComponent(
						"options.iris.shaderPackSelection.copyErrorAlreadyExists",
						fileName
				).mergeStyle(TextFormatting.ITALIC, TextFormatting.RED);

				this.addedPackDialogTimer = 100;
				this.shaderPackList.refresh();

				return;
			} catch (IOException e) {
				Iris.logger.warn("Error copying dragged shader pack", e);

				this.addedPackDialog = new TranslationTextComponent(
						"options.iris.shaderPackSelection.copyError",
						fileName
				).mergeStyle(TextFormatting.ITALIC, TextFormatting.RED);

				this.addedPackDialogTimer = 100;
				this.shaderPackList.refresh();

				return;
			}
		}

		// After copying the relevant files over to the folder, make sure to refresh the shader pack list.
		this.shaderPackList.refresh();

		if (packs.size() == 0) {
			// If zero packs were added, then notify the user that the files that they added weren't actually shader
			// packs.

			if (paths.size() == 1) {
				// If a single pack could not be added, provide a message with that pack in the file name
				String fileName = paths.get(0).getFileName().toString();

				this.addedPackDialog = new TranslationTextComponent(
					"options.iris.shaderPackSelection.failedAddSingle",
					fileName
				).mergeStyle(TextFormatting.ITALIC, TextFormatting.RED);
			} else {
				// Otherwise, show a generic message.

				this.addedPackDialog = new TranslationTextComponent(
					"options.iris.shaderPackSelection.failedAdd"
				).mergeStyle(TextFormatting.ITALIC, TextFormatting.RED);
			}

		} else if (packs.size() == 1) {
			// In most cases, users will drag a single pack into the selection menu. So, let's special case it.
			String packName = packs.get(0).getFileName().toString();

			this.addedPackDialog = new TranslationTextComponent(
					"options.iris.shaderPackSelection.addedPack",
					packName
			).mergeStyle(TextFormatting.ITALIC, TextFormatting.YELLOW);

			// Select the pack that the user just added, since if a user just dragged a pack in, they'll probably want
			// to actually use that pack afterwards.
			this.shaderPackList.select(packName);
		} else {
			// We also support multiple packs being dragged and dropped at a time. Just show a generic success message
			// in that case.
			this.addedPackDialog = new TranslationTextComponent(
					"options.iris.shaderPackSelection.addedPacks",
					packs.size()
			).mergeStyle(TextFormatting.ITALIC, TextFormatting.YELLOW);
		}

		// Show the relevant message for 5 seconds (100 ticks)
		this.addedPackDialogTimer = 100;
	}

	private static void copyShaderPack(Path pack, String name) throws IOException {
		Path target = Iris.SHADERPACKS_DIRECTORY.resolve(name);

		// Copy the pack file into the shaderpacks folder.
		Files.copy(pack, target);
		// Zip or other archive files will be copied without issue,
		// however normal folders will require additional handling below.

		// Manually copy the contents of the pack if it is a folder
		if (Files.isDirectory(pack)) {
			// Use for loops instead of forEach due to createDirectory throwing an IOException
			// which requires additional handling when used in a lambda

			// Copy all sub folders, collected as a list in order to prevent issues with non-ordered sets
			for (Path p : Files.walk(pack).filter(Files::isDirectory).collect(Collectors.toList())) {
				Path folder = pack.relativize(p);

				if (Files.exists(folder)) {
					continue;
				}

				Files.createDirectory(target.resolve(folder));
			}
			// Copy all non-folder files
			for (Path p : Files.walk(pack).filter(p -> !Files.isDirectory(p)).collect(Collectors.toSet())) {
				Path file = pack.relativize(p);

				Files.copy(p, target.resolve(file));
			}
		}

	}

	@Override
	public void onClose() {
		if (!dropChanges) {
			// TODO: Don't apply changes unnecessarily
			applyChanges();
		}

		this.minecraft.displayGuiScreen(parent);
	}

	private void dropChangesAndClose() {
		dropChanges = true;
		onClose();
	}

	private void applyChanges() {
		ShaderPackListWidget.BaseEntry base = this.shaderPackList.getSelected();

		if (!(base instanceof ShaderPackListWidget.ShaderPackEntry)) {
			return;
		}

		ShaderPackListWidget.ShaderPackEntry entry = (ShaderPackListWidget.ShaderPackEntry)base;
		String name = entry.getPackName();
		Iris.getIrisConfig().setShaderPackName(name);
		Iris.getIrisConfig().setShadersEnabled(this.shaderPackList.getEnableShadersButton().enabled);

		try {
			Iris.getIrisConfig().save();
		} catch (IOException e) {
			Iris.logger.error("Error saving configuration file!");
			Iris.logger.catching(e);
		}

		try {
			Iris.reload();
		} catch (IOException e) {
			Iris.logger.error("Error reloading shader pack while applying changes!");
			Iris.logger.catching(e);
		}
	}

	private void openShaderPackFolder() {
		Util.getOSType().openFile(Iris.SHADERPACKS_DIRECTORY.toFile());
	}
}

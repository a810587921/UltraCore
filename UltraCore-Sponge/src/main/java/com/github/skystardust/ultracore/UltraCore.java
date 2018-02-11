package com.github.skystardust.ultracore;

import com.github.skystardust.ultracore.database.DatabaseRegistry;
import com.github.skystardust.ultracore.exceptions.ConfigurationException;
import com.github.skystardust.ultracore.exceptions.DatabaseInitException;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@Plugin(
        id = "ultracore",
        name = "UltraCore",
        version = "1.0.0",
        authors = {
                "SkyStardust"
        },
        description = "UltraCore By SkyStardust"
)
@lombok.Getter
public class UltraCore {

    private Logger logger;

    private static UltraCore ultraCore;


    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        try {
            this.logger = Sponge.getPluginManager().getPlugin("ultracore").orElseThrow(IllegalAccessException::new).getLogger();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        UltraCore.ultraCore = this;
        getLogger().info("Booting...");
        CommandSpec reloadDatabaseCommand = CommandSpec.builder()
                .permission("ultracore.reload")
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("database"))))
                .description(Text.of())
                .executor(new CommandExecutor() {
                    @Override
                    @NonnullByDefault
                    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
                        try {
                            String databaseName = (String) args.getOne("database").orElseThrow(IllegalAccessException::new);
                            DatabaseRegistry.reloadPluginDatabase(databaseName);
                            sendMessage( "重载数据库 " + databaseName + " 成功!");
                        } catch (DatabaseInitException | ConfigurationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }

                        return CommandResult.success();
                    }
                })
                .build();
        CommandSpec mainCommand = CommandSpec.builder()
                .permission("ultracore.base")
                .child(reloadDatabaseCommand, "reload")
                .description(Text.of("UltraCore Main command"))
                .executor((src, args) -> {
                    src.sendMessage(Text.of("wrong args"));
                    return CommandResult.success();
                })
                .build();
        Sponge.getCommandManager().register(this, mainCommand, "ultracore", "uc");
    }

    public static void sendMessage(String str){
        getUltraCore().getLogger().info("[UltraCore] "+str);
    }

    public static UltraCore getUltraCore() {
        return ultraCore;
    }
}

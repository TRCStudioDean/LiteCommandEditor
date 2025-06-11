package studio.trc.bukkit.litecommandeditor.module.tool;

import java.util.List;

import lombok.Getter;

import studio.trc.bukkit.litecommandeditor.configuration.ConfigurationType;
import studio.trc.bukkit.litecommandeditor.message.MessageUtil;

public class ListNamesRecord 
{
    @Getter
    private final String typeName;
    @Getter
    private final List<String> keywords;
    @Getter
    private final List<String> names;

    public ListNamesRecord(String typeName, List<String> keywords, List<String> names) {
        this.typeName = typeName;
        this.keywords = keywords;
        this.names = names;
    }
    
    public String getDisplayTypeName() {
        return MessageUtil.getMessage(ConfigurationType.MESSAGES, "Command-Messages.Tools.List-Names.Types." + typeName);
    }
}

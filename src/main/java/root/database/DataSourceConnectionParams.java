package root.database;

import static root.common.utils.Preconditions.checkArgument;

public record DataSourceConnectionParams(String url, String username, String password, String defaultSchema)
{
    public DataSourceConnectionParams
    {
        checkArgument(defaultSchema != null && !defaultSchema.isBlank(), "Default schema cannot be null or blank");
    }

    @Override
    public String toString()
    {
        return "DataSourceConnectionParams{url='" + url + "', username='" + username + "', password='[HIDDEN]', defaultSchema='" + defaultSchema + "'}";
    }
}
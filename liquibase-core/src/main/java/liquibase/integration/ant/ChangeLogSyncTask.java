package liquibase.integration.ant;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class ChangeLogSyncTask extends AbstractChangeLogBasedTask {
    private String toTag;

    @Override
    public void executeWithLiquibaseClassloader() throws BuildException {
        Liquibase liquibase = getLiquibase();
        OutputStreamWriter writer = null;
        try {
            FileResource outputFile = getOutputFile();
            if (outputFile != null) {
                writer = new OutputStreamWriter(outputFile.getOutputStream(), getOutputEncoding());
                liquibase.changeLogSync(toTag, new Contexts(getContexts()), getLabels(), writer);
            } else {
                liquibase.changeLogSync(toTag, new Contexts(getContexts()), getLabels());
            }
        } catch (UnsupportedEncodingException e) {
            throw new BuildException("Unable to generate sync SQL. Encoding [" + getOutputEncoding() + "] is not supported.", e);
        } catch (IOException e) {
            throw new BuildException("Unable to generate sync SQL. Error creating output writer.", e);
        } catch (LiquibaseException e) {
            throw new BuildException("Unable to sync change log: " + e.getMessage(), e);
        } finally {
            FileUtils.close(writer);
        }
    }

    public String getToTag() {
        return toTag;
    }

    public void setToTag(String toTag) {
        this.toTag = toTag;
    }
}

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminCommandInvoker {
    private final List<String> commandHistory = new ArrayList<>();

    public boolean execute(UserManagementCommand command) {
        if (command == null) {
            return false;
        }

        boolean executed = command.execute();
        if (executed) {
            commandHistory.add(command.getName());
        }
        return executed;
    }

    public List<String> getCommandHistory() {
        return Collections.unmodifiableList(commandHistory);
    }
}

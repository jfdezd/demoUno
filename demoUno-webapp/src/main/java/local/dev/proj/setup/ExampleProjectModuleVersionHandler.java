package local.dev.proj.setup;

import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.SetPropertyTask;
import info.magnolia.module.delta.Task;
import info.magnolia.repository.RepositoryConstants;

import java.util.ArrayList;
import java.util.List;

public class ExampleProjectModuleVersionHandler extends
		DefaultModuleVersionHandler {
	@Override
	protected List<Task> getExtraInstallTasks(InstallContext installContext) {
		ArrayList<Task> tasks = new ArrayList<Task>();
//		SetPropertyTask setPropertyTask = new SetPropertyTask(
//				"Change the rootFolder property of fs-browser-app contentConnector.",
//				RepositoryConstants.CONFIG,
//				"/modules/fs-browser-app/apps/fs-browser/subApps/browser/contentConnector",
//				"rootFolder", "/your-mounted-nas-drive/current-work/magnolia/");
//		tasks.add(setPropertyTask);
		return tasks;
	}
}

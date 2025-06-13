import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.BambooOid;
import com.atlassian.bamboo.specs.api.builders.deployment.Deployment;
import com.atlassian.bamboo.specs.api.builders.deployment.Environment;
import com.atlassian.bamboo.specs.api.builders.deployment.ReleaseNaming;
import com.atlassian.bamboo.specs.api.builders.permission.DeploymentPermissions;
import com.atlassian.bamboo.specs.api.builders.permission.EnvironmentPermissions;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.builders.task.ArtifactDownloaderTask;
import com.atlassian.bamboo.specs.builders.task.CleanWorkingDirectoryTask;
import com.atlassian.bamboo.specs.builders.task.DownloadItem;
import com.atlassian.bamboo.specs.builders.task.ScriptTask;
import com.atlassian.bamboo.specs.util.BambooServer;

@BambooSpec
public class PlanSpec {
    
    public Deployment rootObject() {
        final Deployment rootObject = new Deployment(new PlanIdentifier("PROJ", "PLAN")
                .oid(new BambooOid("eks18dhbeku9")),
            "deployment")
            .oid(new BambooOid("el4i7uo0wb29"))
            .releaseNaming(new ReleaseNaming("release-1")
                    .autoIncrement(true))
            .environments(new Environment("environment")
                    .tasks(new CleanWorkingDirectoryTask(),
                        new ArtifactDownloaderTask()
                            .description("Download release contents")
                            .artifacts(new DownloadItem()
                                    .allArtifacts(true)),
                        new ScriptTask()
                            .inlineBody("echo \"something\"")));
        return rootObject;
    }
    
    public DeploymentPermissions deploymentPermission() {
        final DeploymentPermissions deploymentPermission = new DeploymentPermissions("deployment")
            .permissions(new Permissions()
                    .userPermissions("admin", PermissionType.ADMIN, PermissionType.EDIT, PermissionType.VIEW_CONFIGURATION, PermissionType.VIEW, PermissionType.CLONE, PermissionType.APPROVE_RELEASE, PermissionType.CREATE_RELEASE)
                    .groupPermissions("bamboo-admin", PermissionType.APPROVE_RELEASE, PermissionType.VIEW, PermissionType.EDIT, PermissionType.CLONE, PermissionType.ADMIN, PermissionType.CREATE_RELEASE, PermissionType.VIEW_CONFIGURATION)
                    .loggedInUserPermissions(PermissionType.VIEW, PermissionType.VIEW_CONFIGURATION, PermissionType.CREATE_RELEASE));
        return deploymentPermission;
    }
    
    public EnvironmentPermissions environmentPermission1() {
        final EnvironmentPermissions environmentPermission1 = new EnvironmentPermissions("deployment")
            .environmentName("environment")
            .permissions(new Permissions()
                    .userPermissions("admin", PermissionType.EDIT, PermissionType.VIEW_CONFIGURATION, PermissionType.VIEW, PermissionType.BUILD));
        return environmentPermission1;
    }
    
    public static void main(String... argv) {
        //By default credentials are read from the '.credentials' file.
        BambooServer bambooServer = new BambooServer("https://linux-103335.prod.atl-cd.net/bamboo");
        final PlanSpec planSpec = new PlanSpec();
        
        final Deployment rootObject = planSpec.rootObject();
        bambooServer.publish(rootObject);
        
        final DeploymentPermissions deploymentPermission = planSpec.deploymentPermission();
        bambooServer.publish(deploymentPermission);
        
        final EnvironmentPermissions environmentPermission1 = planSpec.environmentPermission1();
        bambooServer.publish(environmentPermission1);
    }
}
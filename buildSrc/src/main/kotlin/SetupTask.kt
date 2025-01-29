import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.IOException

open class SetupTask : DefaultTask() {
    @TaskAction
    fun setup() {
        val projectDir = project.projectDir
        val repository = try {
            FileRepositoryBuilder.create(projectDir.resolve(".git"))
        } catch (e: IOException) {
            error("Repository not found in $projectDir")
        }
        val git = Git(repository)
        val uri = git.remoteList().call().flatMap { it.urIs }.firstOrNull { it.host == "github.com" }
            ?: error("GitHub repository not found")
        val account = ("/?([^/]*)/?".toRegex().find(uri.path)?.groupValues?.get(1)
            ?: error("Account not found (${uri.path})")).replace("-", "_")
        val groupId = "github.$account.${project.name}"
        val srcDir = projectDir.resolve("src/main/kotlin/${groupId.replace(".", "/")}").apply(File::mkdirs)
        srcDir.resolve("${project.name}Plugin.kt").writeText(
            """
            package $groupId
            
            import org.bukkit.plugin.java.JavaPlugin
            
            class ${project.name.uppercase()}: JavaPlugin() {
                override fun onEnable() {
                    // Plugin startup logic
                }
                
                override fun onDisable() {
                    // Plugin shutdown logic
                }
            }
        """.trimIndent(), Charsets.UTF_8
        )
        val resourceDir = projectDir.resolve("src/main/resources").apply(File::mkdirs)
        resourceDir.resolve("plugin.yml").writeText(
            """
            # this is example plugin.yml file
            name: ${project.name}
            version: ${project.version}
            main: $groupId.${project.name.uppercase()}
            description: ${project.description}
            author: $account
            website: https://github.com/$account/${project.name}
            api-version: ${project.providers.gradleProperty("mcversion").get()}
        """.trimIndent(), Charsets.UTF_8
        ) // README
        projectDir.resolve("README.md").writeText(
            """
            # ${project.name}
            
            ## Requirements
            
            - Java 21
            - Paper ${project.providers.gradleProperty("mcversion").get()} above
            
            ## Features
            
            ## Commands
            
            ## Configurations
            
            ## Downloads
            
            ### [Download it from Release](https://github.com/$account/${project.name}/releases/latest)
        """.trimIndent(), Charsets.UTF_8
        )
    }
}
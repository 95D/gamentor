import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

/**
 * A convention plugins of project's default Android Library modules
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.configure<LibraryExtension> {
                compileSdk = project.property("android.compileSdk").toString().toInt()

                defaultConfig {
                    minSdk = project.property("android.minSdk").toString().toInt()
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    consumerProguardFiles("consumer-rules.pro")
                }

                buildTypes {
                    release {
                        isMinifyEnabled = false
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }

                compileOptions {
                    val javaVersion = JavaVersion.VERSION_11
                    sourceCompatibility = javaVersion
                    targetCompatibility = javaVersion
                }
            }

            extensions.configure<KotlinAndroidProjectExtension> {
                jvmToolchain(11)
            }
        }
    }
}

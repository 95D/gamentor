import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

/**
 * A convention plugins of project's default Android application modules
 */
class AndroidAppConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.configure<ApplicationExtension> {
                compileSdk = project.property("android.compileSdk").toString().toInt()

                defaultConfig {
                    minSdk = project.property("android.minSdk").toString().toInt()
                    targetSdk = project.property("android.targetSdk").toString().toInt()
                    versionCode = project.property("app.versionCode").toString().toInt()
                    versionName = project.property("app.versionName").toString()
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

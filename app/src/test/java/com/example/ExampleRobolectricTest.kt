package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppScanner
import com.example.data.LauncherRepository
import com.example.data.LauncherSettings
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Samsung OneLauncher Pro", appName)
  }

  @Test
  fun `launcher repository saves and loads settings`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = LauncherRepository(context)
    val settings = repo.loadSettings()
    assertNotNull(settings)
    assertTrue(settings.dockPackages.isNotEmpty())

    repo.saveSettings(settings.copy(showLabels = false))
    val reloaded = repo.loadSettings()
    assertEquals(false, reloaded.showLabels)
  }

  @Test
  fun `app scanner returns apps without crashing`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val apps = AppScanner.getInstalledApps(context)
    assertNotNull(apps)
    assertTrue(apps.isNotEmpty())
  }
}

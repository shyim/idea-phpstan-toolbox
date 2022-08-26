package de.shyim.ideaphpstan

import com.intellij.ide.highlighter.XmlFileType
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlFile
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.PsiErrorElementUtil
import com.intellij.util.indexing.FileBasedIndex
import de.shyim.ideaphpstan.index.PhpstanIgnoreIndex
import junit.framework.TestCase

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class PhpstanIgnoreIndexTest : BasePlatformTestCase() {
    override fun getTestDataPath() = "src/test/testData/PhpstanIgnoreIndexTest"

    override fun setUp() {
        super.setUp()
        myFixture.copyFileToProject("phpstan-baseline.neon")
    }

    fun testErrorsAreIndexed() {
        val keys = FileBasedIndex.getInstance().getAllKeys(PhpstanIgnoreIndex.key, project)

        assertEquals(2, keys.size)

        val controller = FileBasedIndex.getInstance()
            .getValues(
                PhpstanIgnoreIndex.key,
                "src/Administration/Controller/AdministrationController.php",
                GlobalSearchScope.allScope(project)
            ).first()

        val deprecation = FileBasedIndex.getInstance()
            .getValues(
                PhpstanIgnoreIndex.key,
                "src/Administration/Controller/DocumentServiceDeprecationController.php",
                GlobalSearchScope.allScope(project)
            ).first()

        assertEquals(3, controller.errors)
        assertEquals(3, controller.start)
        assertEquals(10, controller.end)

        assertEquals(1, deprecation.errors)
        assertEquals(12, deprecation.start)
        assertEquals(15, deprecation.end)
    }
}

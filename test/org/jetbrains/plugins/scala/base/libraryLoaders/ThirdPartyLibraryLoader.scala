package org.jetbrains.plugins.scala.base.libraryLoaders

import java.io.File

import com.intellij.openapi.module.Module
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import com.intellij.testFramework.PsiTestUtil
import org.jetbrains.plugins.scala.base.libraryLoaders.IvyLibraryLoader._
import org.jetbrains.plugins.scala.debugger.{ScalaVersion, Scala_2_11, Scala_2_12}
import org.jetbrains.plugins.scala.project.ModuleExt

/**
  * @author adkozlov
  */
trait ThirdPartyLibraryLoader extends LibraryLoader {
  protected val name: String

  override def init(implicit version: ScalaVersion): Unit = {
    if (alreadyExistsInModule) return

    val path = this.path
    val file = new File(path).getCanonicalFile
    assert(file.exists(), s"library root for $name does not exist at $file")
    VfsRootAccess.allowRootAccess(path)
    PsiTestUtil.addLibrary(module, path)

    LibraryLoader.storePointers()
  }

  protected def path(implicit version: ScalaVersion): String

  private def alreadyExistsInModule =
    module.libraries.map(_.getName)
      .contains(name)
}

abstract class IvyLibraryLoaderAdapter extends ThirdPartyLibraryLoader with IvyLibraryLoader {
  protected val version: String

  override protected def folder(implicit version: ScalaVersion): String =
    s"${name}_${version.major}"

  override protected def fileName(implicit version: ScalaVersion): String =
    s"$folder-${this.version}"
}

abstract class ScalaZBaseLoader(implicit module: Module) extends IvyLibraryLoaderAdapter {
  override val vendor: String = "org.scalaz"
  override val version: String = "7.1.0"
  override val ivyType: IvyType = Bundles
}

case class ScalaZCoreLoader()(implicit val module: Module) extends ScalaZBaseLoader {
  override val name: String = "scalaz-core"
}

case class ScalaZConcurrentLoader()(implicit val module: Module) extends ScalaZBaseLoader {
  override val name: String = "scalaz-concurrent"
}

case class SlickLoader()(implicit val module: Module) extends IvyLibraryLoaderAdapter {
  override val name: String = "slick"
  override val vendor: String = "com.typesafe.slick"
  override val version: String = "3.2.0"
  override val ivyType: IvyType = Bundles

  override def path(implicit version: ScalaVersion): String =
    super.path(Scala_2_11)
}

case class SprayLoader()(implicit val module: Module) extends IvyLibraryLoaderAdapter {
  override val name: String = "spray-routing"
  override val vendor: String = "io.spray"
  override val version: String = "1.3.1"
  override val ivyType: IvyType = Bundles

  override def path(implicit version: ScalaVersion): String =
    super.path(Scala_2_11)
}

case class CatsLoader()(implicit val module: Module) extends IvyLibraryLoaderAdapter {
  override val name: String = "cats-core"
  override val vendor: String = "org.typelevel"
  override val version: String = "0.4.0"

  override def path(implicit version: ScalaVersion): String =
    super.path(Scala_2_11)
}

abstract class Specs2BaseLoader(implicit module: Module) extends IvyLibraryLoaderAdapter {
  override val vendor: String = "org.specs2"
}

case class Specs2Loader(override val version: String)
                       (implicit val module: Module) extends Specs2BaseLoader {
  override val name: String = "specs2"
}

case class ScalaCheckLoader()(implicit val module: Module) extends IvyLibraryLoaderAdapter {
  override val name: String = "scalacheck"
  override val vendor: String = "org.scalacheck"
  override val version: String = "1.12.5"

  override def path(implicit version: ScalaVersion): String =
    super.path(Scala_2_11)
}

case class PostgresLoader()(implicit val module: Module) extends IvyLibraryLoaderAdapter {
  override val name: String = "postgresql"
  override val vendor: String = "com.wda.sdbc"
  override val version: String = "0.5"

  override def path(implicit version: ScalaVersion): String =
    super.path(Scala_2_11)
}

case class ScalaTestLoader(override val version: String,
                           override val ivyType: IvyType = Jars)
                          (implicit val module: Module) extends IvyLibraryLoaderAdapter {
  override val name: String = "scalatest"
  override val vendor: String = "org.scalatest"
}

case class ScalaXmlLoader()(implicit val module: Module) extends IvyLibraryLoaderAdapter {
  override val name: String = "scala-xml"
  override val vendor: String = "org.scala-lang.modules"
  override val version: String = "1.0.1"
  override val ivyType: IvyType = Bundles
}

case class ScalacticLoader(override val version: String,
                           override val ivyType: IvyType = Jars)
                          (implicit val module: Module) extends IvyLibraryLoaderAdapter {
  override val name: String = "scalactic"
  override val vendor: String = "org.scalactic"
}

case class UTestLoader(override val version: String)
                      (implicit val module: Module) extends IvyLibraryLoaderAdapter {
  override val name: String = "utest"
  override val vendor: String = "com.lihaoyi"
}

case class QuasiQuotesLoader()(implicit val module: Module) extends IvyLibraryLoaderAdapter {
  override val name: String = "quasiquotes"
  override val vendor: String = "org.scalamacros"
  override val version: String = "2.0.0"
}

case class ScalaAsyncLoader()(implicit val module: Module) extends IvyLibraryLoaderAdapter {
  override val name: String = "scala-async"
  override val vendor: String = "org.scala-lang.modules"
  override val version: String = "0.9.5"
}
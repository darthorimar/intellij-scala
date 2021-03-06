package org.jetbrains.plugins.cbt.process

import java.io.File

import com.intellij.openapi.externalSystem.model.task.{ExternalSystemTaskId, ExternalSystemTaskNotificationEvent, ExternalSystemTaskNotificationListener}
import com.intellij.openapi.externalSystem.service.notification.{ExternalSystemNotificationManager, NotificationSource}
import com.intellij.openapi.project.Project
import org.jetbrains.plugins.cbt.project.CbtProjectSystem
import org.jetbrains.plugins.cbt.project.settings.{CbtExecutionSettings, CbtProjectSettings}
import org.jetbrains.plugins.cbt.project.structure.CbtProjectImporingException

import scala.sys.process.{Process, ProcessLogger}
import scala.util.{Failure, Success, Try}
import scala.xml.{Elem, XML}

object CbtProcess {
  def buildInfoXml(root: File,
                   settings: CbtExecutionSettings,
                   projectOpt: Option[Project],
                   taskListener: Option[(ExternalSystemTaskId,
                   ExternalSystemTaskNotificationListener)]): Try[Elem] = {
    def buildParams: Seq[String] = {
      val extraModulesStr = settings.extraModules.mkString(":")
      val needCbtLibsStr = settings.isCbt.unary_!.toString
      Seq("--extraModules", extraModulesStr, "--needCbtLibs", needCbtLibsStr)
    }

    val xml =
      runAction("buildInfoXml" +: buildParams, settings.useDirect, root, projectOpt, taskListener)
    xml.map(XML.loadString)
  }

  def generateGiter8Template(template: String, project: Project, root: File): Try[String] =
    runAction(Seq("tools", "g8", template), useDirect = true, root, Option(project), None)

  def runAction(action: Seq[String],
                useDirect: Boolean,
                root: File,
                projectOpt: Option[Project],
                taskListener: Option[(ExternalSystemTaskId,
                  ExternalSystemTaskNotificationListener)]): Try[String] = {
    projectOpt.foreach { project =>
      ExternalSystemNotificationManager.getInstance(project)
        .clearNotifications(NotificationSource.PROJECT_SYNC, CbtProjectSystem.Id)
    }
    val onOutput = (text: String, stderr: Boolean) => {
      if (stderr) {
        taskListener.foreach {
          case (id, l) =>
            l.onStatusChange(new ExternalSystemTaskNotificationEvent(id, text))
        }
      }
    }

    val outputHandler =
      new CbtOutputListener(onOutput, projectOpt, NotificationSource.PROJECT_SYNC)
    val logger = ProcessLogger(
      text => outputHandler.parseLine(text, stderr = false),
      text => outputHandler.parseLine(text + '\n', stderr = true))
    val task = Seq("cbt") ++ (if (useDirect) Seq("direct") else Seq.empty) ++ action
    val exitCode = Process(task, root) ! logger
    exitCode match {
      case 0 =>
        Success(outputHandler.stdOut)
      case _ =>
        Failure(new CbtProjectImporingException(outputHandler.errors.mkString("\n")))
    }
  }

}

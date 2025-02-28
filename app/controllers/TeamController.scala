package controllers

import javax.inject._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n._
import play.api.libs.json._
import services.TeamService
import models.Team

@Singleton
class TeamController @Inject()(
  val controllerComponents: MessagesControllerComponents,
  teamService: TeamService
) extends MessagesBaseController {

  // Format JSON untuk model Team
  implicit val teamFormat: OFormat[Team] = Json.format[Team]

  // Form untuk HTML (Create & Edit)
  val teamForm: Form[Team] = Form(
    mapping(
      "id" -> longNumber, // Tambahkan id ke form
      "namaTim" -> nonEmptyText,
      "negara" -> nonEmptyText,
      "liga" -> nonEmptyText,
      "stadion" -> nonEmptyText,
      "logo" -> nonEmptyText,
      "gambarStadion" -> nonEmptyText
    )(Team.apply)(Team.unapply) // Gunakan apply dan unapply langsung
  )

  // ---------------------- HTML Routes ----------------------

  // Menampilkan daftar tim 
  def index() = Action { implicit request: MessagesRequest[AnyContent] =>
    val teams = teamService.getAllTeams
    Ok(views.html.index(teams))
  }

  // Menampilkan form create 
  def createForm() = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.create(teamForm))
  }

  // Menyimpan tim baru 
  def save() = Action { implicit request: MessagesRequest[AnyContent] =>
    teamForm.bindFromRequest().fold(
      formWithErrors => BadRequest(views.html.create(formWithErrors)),
      team => {
        teamService.addTeam(team) // Simpan tim dengan id yang ditentukan pengguna
        Redirect(routes.TeamController.index)
      }
    )
  }

  // Menampilkan form edit 
  def editForm(id: Long) = Action { implicit request: MessagesRequest[AnyContent] =>
    teamService.getTeamById(id) match {
      case Some(team) => Ok(views.html.edit(id, teamForm.fill(team)))
      case None => NotFound("Team not found")
    }
  }

  // Mengupdate tim 
  def update(id: Long) = Action { implicit request: MessagesRequest[AnyContent] =>
    teamForm.bindFromRequest().fold(
      formWithErrors => BadRequest(views.html.edit(id, formWithErrors)),
      team => {
        val updatedTeam = team.copy(id = id) // Gunakan id yang dikirim oleh pengguna
        if (teamService.updateTeam(id, updatedTeam)) {
          Redirect(routes.TeamController.index)
        } else {
          NotFound("Team not found")
        }
      }
    )
  }

  // Menghapus tim 
  def delete(id: Long) = Action {
    if (teamService.deleteTeam(id)) {
      Redirect(routes.TeamController.index)
    } else {
      NotFound("Team not found")
    }
  }

  // ---------------------- JSON REST API ----------------------

  // Mendapatkan daftar semua tim dalam format JSON
  def getAllTeams() = Action {
    Ok(Json.toJson(teamService.getAllTeams))
  }

  // Mendapatkan tim berdasarkan ID dalam format JSON
  def getTeam(id: Long) = Action {
    teamService.getTeamById(id) match {
      case Some(team) => Ok(Json.toJson(team))
      case None => NotFound(Json.obj("error" -> "Team not found"))
    }
  }

  // Menambahkan tim baru melalui JSON API
  def addTeam() = Action(parse.json) { request =>
    request.body.validate[Team].map { team =>
      teamService.addTeam(team) // Simpan tim dengan id yang ditentukan pengguna
      Created(Json.toJson(team))
    }.recoverTotal { e =>
      BadRequest(Json.obj("error" -> JsError.toJson(e)))
    }
  }

  // Mengupdate tim berdasarkan ID melalui JSON API
  def updateTeam(id: Long) = Action(parse.json) { request =>
    request.body.validate[Team].map { team =>
      val updatedTeam = team.copy(id = id)
      if (teamService.updateTeam(id, updatedTeam)) {
        Ok(Json.toJson(updatedTeam))
      } else {
        NotFound(Json.obj("error" -> "Team not found"))
      }
    }.recoverTotal { e =>
      BadRequest(Json.obj("error" -> JsError.toJson(e)))
    }
  }

  // Menghapus tim berdasarkan ID melalui JSON API
  def deleteTeam(id: Long) = Action {
    if (teamService.deleteTeam(id)) {
      Ok(Json.obj("message" -> "Team deleted"))
    } else {
      NotFound(Json.obj("error" -> "Team not found"))
    }
  }
}

package services

import javax.inject._
import models.Team
import scala.collection.mutable.ListBuffer

@Singleton
class TeamService {

  private val teams = ListBuffer(
    Team(1, "Manchester United", "England", "Premier League", "Old Trafford", "https://upload.wikimedia.org/wikipedia/id/thumb/7/7a/Manchester_United_FC_crest.svg/1200px-Manchester_United_FC_crest.svg.png", "https://cdn.rri.co.id/berita/1/images/1680951519000-C17D9D57-0287-4801-9EFD-10183BC68913/1680951519000-C17D9D57-0287-4801-9EFD-10183BC68913.jpeg")
  )

  private var lastId: Long = teams.map(_.id).max

  def getNextId: Long = {
    lastId += 1
    lastId
  }

  def getAllTeams: Seq[Team] = teams.toList

  def getTeamById(id: Long): Option[Team] = teams.find(_.id == id)

  def addTeam(team: Team): Unit = teams += team

  def updateTeam(id: Long, updatedTeam: Team): Boolean = {
    getTeamById(id) match {
      case Some(_) =>
        val index = teams.indexWhere(_.id == id)
        teams.update(index, updatedTeam)
        true
      case None => false
    }
  }

  def deleteTeam(id: Long): Boolean = {
    val initialSize = teams.size
    teams --= teams.filter(_.id == id)
    initialSize > teams.size
  }
}

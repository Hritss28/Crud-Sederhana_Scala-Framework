package models
import play.api.libs.json._

case class Team(
  id: Long,
  namaTim: String,
  negara: String,
  liga: String,
  stadion: String,
  logo: String,
  gambarStadion: String
)


object Team {
  var teams: Seq[Team] = Seq(
    Team(1, "Manchester United", "England", "Premier League", "Old Trafford", "https://upload.wikimedia.org/wikipedia/id/thumb/7/7a/Manchester_United_FC_crest.svg/1200px-Manchester_United_FC_crest.svg.png", "https://cdn.rri.co.id/berita/1/images/1680951519000-C17D9D57-0287-4801-9EFD-10183BC68913/1680951519000-C17D9D57-0287-4801-9EFD-10183BC68913.jpeg"),
  )
  implicit val teamFormat: OFormat[Team] = Json.format[Team]
}
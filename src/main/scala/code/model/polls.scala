package code.model

import _root_.net.liftweb.mapper._
import   net.liftweb.common.{Box,Full,Empty}
import java.text.SimpleDateFormat

object Choice extends Choice 
with LongKeyedMetaMapper[Choice] 
with CRUDify[Long,Choice]
{
  override def dbTableName = "choices"
}

class Choice extends LongKeyedMapper[Choice] with IdPK {
  def getSingleton = Choice
  object choice extends MappedString(this, 200)
  object votes extends MappedInt(this)
  object poll extends MappedLongForeignKey(this, Poll) {
    override def dbColumnName = "poll_id"
    override def dbIndexed_? = true
    override def validSelectValues = {
      Full(for (opcion <- Poll.findAll) yield (opcion.id.is,opcion.question.is))
    }
  }
}

object Poll extends Poll 
with LongKeyedMetaMapper[Poll] 
with CRUDify[Long, Poll]
{
  override def dbTableName = "polls"
}

class Poll extends LongKeyedMapper[Poll] with IdPK with OneToMany[Long, Poll] {
  def getSingleton = Poll
  object question extends MappedString(this, 200)
  object pubDate extends MappedDateTime(this) {
    import java.util.Date
    import java.text.{SimpleDateFormat,ParseException}
    val sdf = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss")

    override def parse(datetime: String): Box[Date] = {
      try {
        Full(sdf.parse(datetime))
      } catch {
        case e: ParseException => Empty
      }
    }

    override def format(datetime: Date): String = sdf.format(datetime)

  }
  object choices extends MappedOneToMany(Choice, Choice.poll)
}
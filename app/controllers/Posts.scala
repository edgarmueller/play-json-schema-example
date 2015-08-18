package controllers

import com.eclipsesource.schema.{SchemaType, SchemaValidator, _}
import models.Post
import play.api.data.mapping.{VA, Path}
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc.{Action, Controller}


// example based on 'Play for Scala' (http://www.manning.com/hilton/)
class Posts extends Controller {

  def schema = Json.fromJson[SchemaType](Json.parse(
    """{
      |"properties": {
      |  "id":    { "type": "integer" },
      |  "title": { "type": "string", "minLength": 3 },
      |  "body":  { "type": "string" }
      |}
    |}""".stripMargin)).get

  def list = Action {
    val posts = Post.findAll
    Ok(Json.toJson(posts))
  }

  def delete(id: Long) = Action {
    Post.delete(id)
    Ok("Deleted")
  }
  
  def save = Action(parse.json) { implicit request =>
    val json: JsValue = request.body
    println(Json.prettyPrint(json))
    val result: VA[Post] = SchemaValidator.validate(schema, json, Post.reads)
    result.fold(
      valid = { post =>
        Post.save(post)
        Ok(Json.toJson(post))
      },
      invalid = { errors: Seq[(Path, Seq[ValidationError])] =>
        BadRequest(JsError.toJson(errors.toJsError))
      }
    )
  }

}

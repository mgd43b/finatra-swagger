package com.jakehschwartz.finatra.swagger

import io.swagger.v3.oas.models._
import io.swagger.v3.oas.models.media.{Content, MediaType}
import io.swagger.v3.oas.models.parameters._
import io.swagger.v3.oas.models.responses.ApiResponse

import scala.jdk.CollectionConverters._
import scala.reflect.runtime.universe._

object FinatraOperation {
  implicit def convert(operation: Operation): FinatraOperation = new FinatraOperation(operation)
}

class FinatraOperation(operation: Operation) {

  def pathParam[T: TypeTag](name: String, description: String = "", required: Boolean = true)
                           (implicit swagger: FinatraSwagger): Operation = {
    val param = new PathParameter()
      .name(name)
      .description(description)
      .required(required)
      .schema(swagger.registerModel[T].schema)

    operation.addParametersItem(param)
  }

  def request[T <: Product : TypeTag](implicit swagger: FinatraSwagger): Operation = {
    operation.setParameters(swagger.register[T].asJava)

    operation
  }

  def queryParam[T: TypeTag](name: String, description: String = "", required: Boolean = true)
                            (implicit swagger: FinatraSwagger): Operation = {
    val param = new QueryParameter()
      .name(name)
      .description(description)
      .required(required)
      .schema(swagger.registerModel[T].schema)

    operation.addParametersItem(param)
  }

  def headerParam[T: TypeTag](name: String, description: String = "", required: Boolean = true)
                             (implicit swagger: FinatraSwagger): Operation = {
    val param = new HeaderParameter()
      .name(name)
      .description(description)
      .required(required)
      .schema(swagger.registerModel[T].schema)

    operation.addParametersItem(param)
  }

  def cookieParam[T: TypeTag](name: String, description: String = "", required: Boolean = true)
                             (implicit swagger: FinatraSwagger): Operation = {
    val param = new CookieParameter()
      .name(name)
      .description(description)
      .required(required)
      .schema(swagger.registerModel[T].schema)

    operation.addParametersItem(param)
    operation
  }

  def bodyParam[T: TypeTag](description: String = "", example: Option[T] = None)
                           (implicit swagger: FinatraSwagger): Operation = {
    val model = swagger.registerModel[T].schema

    val content = new Content
    val mediaType = new MediaType()
      .schema(model)
    content.addMediaType("application/json", example.fold(mediaType)(mediaType.example))

    val reqBody = new RequestBody()
      .content(content)
      .description(description)
      .$ref(model.get$ref())
    operation.requestBody(reqBody)

    operation
  }

  def respondsWith[T: TypeTag](status: Int,
                               description: String = "",
                               contentType: String = "",
                               example: Option[T] = None)
                              (implicit finatraSwagger: FinatraSwagger): Operation = {
    val ref = finatraSwagger.registerModel[T].schema

//    //todo not working, sample is not in the generated api, waiting for swagger fix
//    example.foreach { e =>
//      if(ref != null) {
//        ref.setExample(e)
//        //val model = api.swagger.getDefinitions.get(ref.asInstanceOf[RefProperty].getSimpleRef)
//        //model.setExample(example)
//      }
//    }

    val content = new Content
    val mediaType = new MediaType().schema(ref)
    content.addMediaType(contentType, example.fold(mediaType)(mediaType.example))

    val apiResponse = new ApiResponse()
      .description(description)
      .content(content)

    operation.getResponses.addApiResponse(status.toString, apiResponse)

    operation
  }

  def tags(tags: List[String]): Operation = {
    operation.setTags(tags.asJava)
    operation
  }
}

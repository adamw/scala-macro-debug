package com.softwaremill.debug.tools

import scala.io.Source
import scala.collection.mutable.StringBuilder
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Stack

trait Tokenizer extends Iterator[String] {

	def nextToken: Option[String]

	private var nextElement: Option[String] = null

	private def step = {
		if(nextElement == null)
			nextElement = nextToken

		nextElement
	}

	override def hasNext: Boolean = step map {_ => true} getOrElse false

	override def next(): String = {
		val r = step

		nextElement = null

		r getOrElse {throw new IndexOutOfBoundsException()}
	}
}

abstract class Filter(source: Tokenizer) extends Tokenizer
{

	private val backup: Stack[String] = Stack[String]()

	protected def nextFromSource:Option[String] =
		if(backup.isEmpty) source.nextToken
		else Some(backup.pop)

	protected def pushback(token: String) = backup push token


	protected def everythingTo(finish: String): Option[String] = {
		val a = nextFromSource

		a map { aval =>
				val r = new StringBuilder()

				var done = false
				var nval = aval

				while(!done)
				{
					if(nval == finish)
					{
						r.append(nval)
						done = true
					}
					else
					{
						val posibleEndAccumulator = ArrayBuffer(nval)
						def posibleEnd = posibleEndAccumulator.mkString("");
						var nextPosibleEndComponent = nextFromSource

						while (finish.startsWith(posibleEnd) && finish != posibleEnd && nextPosibleEndComponent.isInstanceOf[Some[String]]) {
							posibleEndAccumulator += nextPosibleEndComponent.get
							nextPosibleEndComponent = nextFromSource
						}

						nextPosibleEndComponent match {
							case Some(npeval) => pushback(npeval)
							case None =>
						}

						if(posibleEnd == finish || !nextPosibleEndComponent.isInstanceOf[Some[String]]) {
							r.append(posibleEnd)
							done = true
						}
						else {
							r.append(nval)
							posibleEndAccumulator.tail.reverse.foreach{pushback(_)}
						}
					}

					if(!done) {
						val n = nextFromSource

						n match {
							case None => done = true
							case Some(nvaltmp) => nval = nvaltmp
						}
					}
				}

				r.toString
		}
	}
}

class SourceTokenizer(source: Iterator[Char]) extends Tokenizer
{
	override def nextToken =
		if(source.hasNext) Some(source.next.toString)
		else None
}

class StringsAndCommentsTokenizer(source: Tokenizer) extends Filter(source)
{
	var simpleStringMode = false

	override protected def nextFromSource:Option[String] = {
		if(simpleStringMode) {
			val a = super.nextFromSource
			a match {
				case None => a
				case Some(aval) =>
					if(aval == "\\"){

						val b = super.nextFromSource

						b match {
							case None =>
								a
							case Some(bval) =>
								Some(aval + bval)
						}
					}
					else a
			}
		}
		else super.nextFromSource
	}

	override def nextToken: Option[String] =
	{
		val a = nextFromSource

		a match {
			case Some(aval) =>

				if (aval == "/")
				{
					val b = nextFromSource

					b match
					{
						case Some(bval) =>
							if(bval == "/")
							{
								everythingTo("\n")
								Some("\n")
							}
							else if(bval == "*")
							{
								everythingTo("*/")
								nextToken
							}
							else
							{
								pushback(bval)
								a
							}

						case None =>
							a
					}
				}
				else if (aval == "\"")
				{
					val b = nextFromSource

					b match
					{
						case Some(bval) =>
							if(bval == "\"")
							{
								val c = nextFromSource

								c match {
									case None => Some("\"\"")
									case Some(cval) =>
										if (cval == "\"")
										{
											val tail = everythingTo("\"\"\"")
											tail match {
												case None => Some("\"\"\"")
												case Some(tval) =>
													var numberOfExtraQuotes = 0
													var checkForQuotes = true

													while(checkForQuotes) {
														val n = nextFromSource
														n match {
															case None => checkForQuotes = false
															case Some(nval) =>
																if(nval == "\"")
																	numberOfExtraQuotes+=1
																else
																{
																	pushback(nval)
																	checkForQuotes = false
																}
														}
													}

													Some("\"\"\"" + tval + ("\"" * numberOfExtraQuotes))
											}
										}
										else
										{
											pushback(cval)
											Some("\"\"")
										}
								}
							}
							else
							{
								val base = {
									if(bval == "\\") {
										val c = nextFromSource
										c match {
											case None => aval + bval
											case Some(cval) => aval + bval + cval
										}
									}
									else
										aval + bval
								}

								simpleStringMode = true

								val tail = everythingTo("\"")

								simpleStringMode = false

								Some(base + tail.getOrElse(""))
							}

						case None =>
							a
					}
				}
				else
				{
					a
				}

			case None => None
		}
	}
}

class Pairer(source: Tokenizer) extends Filter(source) {

	private val pairs = Map[String, String]("(" -> ")", "[" -> "]", "{" -> "}")

	override def nextToken: Option[String] = {

		val a = nextFromSource

		a match {
			case None => a
			case Some(aval) =>
				if(pairs.contains(aval))
				{
					var n = a
					val buffer = ArrayBuffer[String]()
					var done = false
					val pair = pairs(aval)

					while(!done){
						n match {
							case None => done = true
							case Some(nval) =>
								buffer += nval
								if(nval == pair)
									done = true
						}

						if(!done)
							n = nextToken
					}

					Some(buffer.mkString(""))
				}
				else
					a
		}
	}

}

class ParameterPicker(source: Tokenizer) extends Filter(source) {


	override def nextToken: Option[String] = {
		val a = nextFromSource

		a match {
			case None => a
			case Some(_) =>

				var n = a
				val buffer = ArrayBuffer[String]()
				var done = false

				while(!done) {
					n match {
						case None => done = true
						case Some(nval) =>

							if(nval == "," || nval == ")" || nval == "}")
								done = true
							else if(nval == "\n")
								buffer += " "
							else
								buffer += nval
					}

					if(!done)
						n = nextFromSource
				}

				Some(buffer.mkString(""))
		}
	}

}

class ParametersStream(in: Iterator[Char]) extends Tokenizer {

	def this(source: String) = this(source.toIterator)


	private val source = new ParameterPicker(new Pairer(new StringsAndCommentsTokenizer(new SourceTokenizer(in))))

	def nextToken: Option[String] = source.nextToken

}


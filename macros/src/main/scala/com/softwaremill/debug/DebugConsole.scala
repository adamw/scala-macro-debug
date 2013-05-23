package com.softwaremill.debug

import language.experimental.macros
import reflect.macros.Context
import collection.JavaConversions._
import scala.io.Source
import java.io.File
import scala.io.BufferedSource
import com.softwaremill.debug.tools.ParametersStream




/**
 * Contains the debug method
 */
object DebugConsole {



	/** This prefix will be printed at the beginning of any debug line */
	private val DEBUG_PREFIX = "|D| "





	/**
	 * If all the debugging reports should be removed from the resulting
	 * bytecode. It can be specified through the system property
	 * "enable_debug_messages" or the environment variable
	 * "enable_debug_messages". The system property takes precedence over the
	 * environment variable.
	 */
	private val debugDisabled = {

		// Holds true or false if debugging has been enabled or disabled through
		// a system property
		val systemProperty = {
			val tmp = System.getProperty("enable_debug_messages")
			if(tmp == null) null
			else {
				tmp.trim().toLowerCase() match {
					case "true" => true
					case "false" => false
					case _ => null
				}
			}
		}


		// Holds true or false if debugging has been enabled or disabled through
		// an environment variable
		val environmentProperty = {
			val tmp = System.getenv("enable_debug_messages")
			if(tmp == null) null
			else {
				tmp.trim().toLowerCase() match {
				case "true" => true
				case "false" => false
				case _ => null
				}
			}
		}

		// The resulting value
		systemProperty match {
			case b: Boolean => !b
			case _ =>
				environmentProperty match {
					case b: Boolean => !b
					case _ => false
				}
		}
	}





	/**
	 * Prints debug information in a multy-lined format. If the debugging is
	 * disabled, it does nothing. If no parameters are supplied, it prints the
	 * file and line where the debug invocation is placed. If the first
	 * parameteris a constant, it will be used as a title. All the other
	 * parameters are the debugged variables/expressions.
	 */
	def debugReport(params: Any*): Unit = macro debugReport_impl





	/**
	 * The real implementation of the macro. To make things easy, a helper class
	 * is created which has the full implementation.
	 */
	def debugReport_impl(c: Context)(params: c.Expr[Any]*): c.Expr[Unit] =
	{
		val helper = new Helper[c.type](c)

		helper.debugReport_impl(params:_*)
	}



	def debug(params: Any*): Unit = macro debug_impl

	def debug_impl(c: Context)(params: c.Expr[Any]*): c.Expr[Unit] =
	{
		val helper = new Helper[c.type](c)

		helper.debug_impl(params:_*)
	}





	/**
	 * Contains the real implementation. Allows to split the implementation
	 * of the algorithm in several methods.
	 */
	private class Helper[C <: Context](val c: C)
	{

		import c.universe._




		/**
		 * The real implementation of the debugReport method
		 */
		def debugReport_impl(params: c.Expr[Any]*): c.Expr[Unit] =
		{
			if(debugDisabled)
				createEmpty
			else if(params.length == 0)
				createPrintPosition
			else
				createMultilineDebug(params:_*)
		}

		/**
		 * The real implementation of the debugReport method
		 */
		def debug_impl(params: c.Expr[Any]*): c.Expr[Unit] =
		{
			if(debugDisabled)
				createEmpty
			else if(params.length == 0)
				createPrintPosition
			else
				createSingleLineDebug(params:_*)
		}



		/**
		 * Creates an empty Expr
		 */
		private def createEmpty =
				reify{}




		/**
		 * Creates the Expr for the code that prints the file and line where
		 * the debug has been called
		 */
		private def createPrintPosition =
				createPrint(DEBUG_PREFIX +
					c.enclosingUnit.source.file.path + " (line " +
					c.enclosingPosition.line + ")")




		/**
		 * Creates the Expr for the code that prints an optional title and
		 * some variables. The variables are printed as "Label = Value", where
		 * 'label' is the "text" in the debug call and 'value' is the result
		 * of evaluating that expression
		 */
		private def createMultilineDebug(params: c.Expr[Any]*): c.Expr[Unit] =
		{
			val hasTitle = params(0).tree match {
				case Literal(Constant(const))    =>   true
				case _                           =>   false
			}

			val firstVariableIndex =
					if(hasTitle) 1 else 0

			val maxLabelLength =
				if(params.length - firstVariableIndex == 0)
					0
				else
					params
						.zip(getParameterLabels(params.length))
						.slice(firstVariableIndex, params.length)
						.map{case (param, label) => label.length()}
						.max

			val tab =
				if(params.length == 1 || ! hasTitle)
					""
				else
					" " * 4

			val trees = params.zip(getParameterLabels(params.length)).zipWithIndex.map
			{ case ((param, label), i) =>

				val msg =
					if(hasTitle && i == 0) DEBUG_PREFIX
					else
					{
						val padding = " " * (maxLabelLength - label.length())

						DEBUG_PREFIX + tab + label + padding + " = "
					}

				createPrint(msg, param, true).tree
			}


			c.Expr[Unit](Block(trees.toList, Literal(Constant(()))))
		}

		private def findMin(tree: c.Tree): c.Position = {
			if(tree.children.isEmpty)
				tree.pos
			else{
				tree.children.map{t => findMin(t)}.foldLeft(tree.pos){(pos, tree) =>
					if(tree.pos.line < pos.line) tree.pos
					else if(pos.line < tree.pos.line) pos
					else if(pos.column < tree.pos.column) pos
					else tree.pos
				}
			}
		}

		private def nextBlock(stream: BufferedSource): String = {
			val r = new StringBuilder()

			val c = ""

			r.toString()
		}

		private def nextParameter(stream: BufferedSource): String = {
			val r = new StringBuilder()

			

			r.toString()
		}

		private def getParameterLabels(n: Int) = {

			val stream = Source.fromFile(new File(c.enclosingUnit.source.file.path))

			for(i <- 0 until (c.enclosingPosition.line - 1))
				while(stream.hasNext && stream.next != '\n') ();

				val min = c.enclosingPosition.column
	
				{
					var i = 0

					while(i < min) {
						val c = stream.next
						if(c == '\t') i += 8
						else i += 1
					}
				}

			val parameterGetter = new ParametersStream(stream)

			(0 until n).map {num => parameterGetter.nextToken.getOrElse(c.abort(c.enclosingPosition, "Could not parse the parameter labels from source file")).trim()}.toArray

		}


		private def createSingleLineDebug(params: c.Expr[Any]*): c.Expr[Unit] =
		{

			val trees = params.zip(getParameterLabels(params.length)).zipWithIndex.map
			{ case ((param, label), i) =>

				val isConstant = param.tree match {
					case Literal(Constant(const))    =>   true
					case _                           =>   false
				}

				val msg =
					(if(i == 0) DEBUG_PREFIX else ", ") +
					(if(!isConstant) label + " = " else "")

				createPrint(msg, param, i >= params.length - 1).tree
			}


			c.Expr[Unit](Block(trees.toList, Literal(Constant(()))))
		}




		/** Creates the Expr that contains the supplied as a constant */
		private def prepare(in: String) =
				c.Expr[String](Literal(Constant(in)))




		/** Creates the Expr that inserts a println with the supplied string */
		private def createPrint(msg: String) =
				reify {println(prepare(msg).splice)}




		/**
		 * Creates the Expr that inserts one "print" followed by a "println".
		 * The first one prints the supplied msg, the second one prints the
		 * result of evaluating the supplied param.
		 */
		private def createPrint(msg: String, param: c.Expr[Any], newLine: Boolean) =
			if(newLine)
				reify {print(prepare(msg).splice); println(param.splice)}
			else
				reify {print(prepare(msg).splice); print(param.splice)}

	}


}

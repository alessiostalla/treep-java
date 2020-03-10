# The Treep language - reference implementation #

Treep is a general-purpose programming language that revolves around **Tree Processing**.
It draws heavy inspiration from the Lisp family of languages, originating from the seminal work of John McCarthy and
his students on the language LISP, for LISt Processing.

The key design decision for Treep is to be a "programmable programming language" from the start and down to the core.
Its aim is to incorporate modern (ish) techniques in parsing and compiling and offer an API to control and extend the
parser and the compiler themselves, and thus the language itself. Treep is therefore both a language and a
meta-language; in other words, it is homoiconic.

The intended ideal application domain for treep is **Tree Processing**, that is, translation and decoration of syntax tree data structures. This includes, but is not limited to:
* interpreters
* compilers
* transpilers and other kinds of translators
* domain-specific languages (DSLs)
* static and dynamic analysis tools
* editors and other tools for languages and DSLs

We believe that all high-level programming is a form of language design and implementation (textual or graphical).
We want to give developers better tools to implement languages: DSLs, command languages, configuration languages, etc.
and the tools supporting them: editors, IDEs, debuggers, analysis tools, etc.

These are the key concepts of Treep:
* A syntax to represent trees:
  * a concrete, text-based syntax based on indentation a-la Python but with optional use of parentheses for grouping,
    like Lisp
  * an abstract, structured, object-based syntax tree that can be fully manipulated by user programs and that retains parsing-related metadata (file name, line number, original text and so on)
* First-class symbols that can model arbitrarily deep name hierarchies such as module:package:class:member
* Sensible and useful scoping, namespacing and modularization. Too many languages get these wrong!
* Immutability by default
  * Bindings are pure functions
  * Mutable bindings are modeled with places/refs
  * Conses are immutable (but can contain places/refs)
* Typing at the function level, dynamically checked but statically verifiable in a subset of cases, with clear warnings
* First-class environments (also known as symbol tables) complete with type inference information and extensible with user-defined annotations
* Macros (functions for tree transformations at compile time)
* Rich compiler API: the compiler is a service that can be invoked and it has hooks for user-defined transformations
  and optimizations


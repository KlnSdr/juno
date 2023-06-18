# juno
a simple script "language" with 27 instructions
## table of contents
- [usage](#usage)
  - [options](#options)
- [keywords](#keywords)
- [notes](#notes)
  - [variables and scopes](#variables-and-scopes)
  - [types](#types)
  - [functions](#functions)
  - [packages](#packages)
  - [unsafe mode](#unsafe-mode)
  - [comments](#comments)
  - [if statement](#if-statement)
  - [strings](#strings)
  - [loops](#loops)
- [examples](#examples)
  - [hello world](#hello-world)
  - [add to numbers](#add-to-numbers)
  - [if statement](#if-statement)
    - [integer](#integer)
    - [float](#float)
    - [string](#string)
  - [loop](#loop)
    - [check after](#check-after)
    - [check before](#check-before)
  - [function](#function)
  - [packages](#packages)
  - [unsafe mode](#unsafe-mode)
  - [scopes](#scopes)
## usage
to run a script type `java -jar path/to/juno.jar [options]` into your terminal
### options
| option     | description                                                                                     |
|------------|-------------------------------------------------------------------------------------------------|
| -f/--file  | the path to the script you want to run                                                          |
| -c/--calls | the maximum number of instructions that can be executed before the interpreter stops the script |
## keywords
| keyword | parameters                         | description                                                                                                                     |
|---------|------------------------------------|---------------------------------------------------------------------------------------------------------------------------------|
| set     | varname, type, value               | define a new variable of *type* with identifier *name* and *value*                                                              |
| mir     | nameTo, nameFrom                   | mirrors *nameFrom* into *nameTo*                                                                                                |
| prg     | varname                            | deletes the given variable                                                                                                      |
| out     | val1, ..., valN                    | prints the values to the output                                                                                                 |
| add     | varSave, val1, ..., valN           | adds all values together and saves them in *varSave*                                                                            |
| sub     | varSave, val1, ..., valN           | subtracts all values from each other and saves them in *varSave*                                                                |
| mlt     | varSave, val1, ..., valN           | multiplies all values together and saves them in *varSave*                                                                      |
| div     | varSave, val1, ..., valN           | divides all values from each other and saves them in *varSave*                                                                  |
| mod     | varSave, val1, ..., valN           | calculates the modulo of all values and saves them in *varSave*                                                                 |
| shl     | varSave, val1, ..., valN           | shifts all values to the left and saves them in *varSave*                                                                       |
| shr     | varSave, val1, ..., valN           | shifts all values to the right and saves them in *varSave*                                                                      |
| lsb     | varSave val                        | saves the least significant bit of *val* in *varSave*                                                                           |
| msb     | varSave, val1                      | saves the most significant bit of *val* in *varSave*                                                                            |
| con     | varSave, val1, ..., valN           | concatenates all values and saves them in *varSave*                                                                             |
| scp     | scopeName                          | changes the current scope to *scopeName*, if it doesn't exist it is created                                                     |
| dscp    | scopeName                          | deletes the scope *scopeName*                                                                                                   |
| end     |                                    | ends the program                                                                                                                |
| unsafe  |                                    | enables unsafe mode                                                                                                             |
| ld      | fileName                           | runs *fileName* as a script. used to import function from packages                                                              |
| loop    |                                    | starts a loop (currently loops inside loops are not supported)                                                                  |
| pool    |                                    | end of the loop definition                                                                                                      |
| break   |                                    | exit the current loop after the current iteration has finished                                                                  |
| dec     | functionName > param1, ..., paramN | defines a new function with the given name and parameters. each parameter name has to be succeeded by a type (`:s`, `:i`, `:f`) |
| dn      |                                    | end of function definition                                                                                                      |
| !       | functionName, param1, ..., paramN  | calls the function with the given name and parameters                                                                           |
| if      | var1, operator, var2               | compares the two variables with the given operator. if statements are typed like parameters in functions                        |
| fi      |                                    | end of if statement                                                                                                             |

## notes
### variables and scopes
- at the beginning the script is in the `main` scope
- there are three reserved function names: main, global and loop
- you can't access the variables in other scopes without changing the scope. except for the global scope, which can be accessed from every scope
- to use the value of a local variable put a `&` in front of the variable name
- to use the value of a global variable put a `*` in front of the variable name
- to define a new variable in the global scope put a `_` in front of the variable name when calling `set` or `mir`
### types
- `i` for integer
- `f` for float
- `s` for string
- there are currently no other types like boolean
- arrays can be added using the [array.juno](https://github.com/KlnSdr/juno-packages/tree/main/array) package
### functions
- currently functions inside functions are not supported
- anonymous functions are not supported
- functions can be accessed from every scope
- when called each function creates a scope with its own name as identifier. this means that recursive functions are not possible
- the given parameters are copied into the function scope
- along with the parameters the function also gets a variable containing the name of the scope it was called from. this variable is called `callScope`
### packages
- packages are just scripts that can be imported using `ld`
- packages can contain functions and normal code
- when importing a package which was already imported the import is aborted
### unsafe mode
- per default unsafe mode is disabled
- the interpreter keeps track of the number of instructions executed. if the number exceeds the maximum (default: 100.000) the interpreter stops the execution
- unsafe mode can't be disabled again
- :warning: unsafe mode removes the protection from infinite loops
### comments
- comments start with `#` and end with a newline
- the `#` can be escaped using `\` -> this eliminates the usage of `#` in strings (sorry)
### if statement
- the if statement is typed like a function parameter
- the if statement can be used with the types `i`, `f` and `s`
- the if statement can only compare two variables
- the if statement can only compare variables of the same type
- the operators `<`, `>`, `=` and `!` are supported and can be chained at will
  - using `!` multiple times in a row will negate the result multiple times
- strings are compared by their length
- to compare if two strings are equal use `=`
- to check if two strings have the same length use `<!>` or some other combination
- else statements are not supported
- in loops the if statement can be used to check if the loop should be exited or not
### strings
- only strings containing spaces have to be put in quotes (`"`)
### loops
- the break keyword only signals that the loop should be aborted **after** the current iteration
- to exit the loop immediately use `break` in conjunction with `end`
  - this works because loop is treated as a function call internally

## examples
### hello world
```
set msg s "Hello World!"
out &msg
```
### add to numbers
```
set a i 5
set b i 10
add c &a &b
out &c # 15
```
### if statement
#### integer
```
set a i 5
set b i 10
if:i &a < &b
  out &a
fi
```
#### float
```
set a f 5.5
set b f 10.5
if:f &a < &b
  out &a
fi
```
#### string
the string version of if compares the length of the strings. equality is tested with `=`
```
set a s "Hello"
set b s "World"
if:s &a = &b
  out "a and b are the same"
fi

if:s &a < &b
  out "a is shorter than b"
fi
```
### loop
#### check after
```
set a i 0
loop
  add a &a 1
  out &a
  if:i &a = 10
    break
  fi
pool
```
#### check before
```
set a i 0
loop
  if:i &a = 10
    break
    end
  fi
  out &a
  add a &a i
pool
```
### function
```
dec add > a:i b:i
  add c &a &b
  out &c
dn

set a i 5
set b i 10
!add &a &b
```
### packages
#### package.juno
```
dec add > a:i b:i
  add c &a &b
  out &c
dn
```
#### main.juno
```
ld package.juno
set a i 5
set b i 10
!add &a &b
```
### unsafe mode
```
unsafe
loop
  out "to infinity and beyond"
pool
```
### scopes
#### local scopes
```
scp main
set msg s "Hello World!"
out &msg # Hello World!

scp sub # change scope from main to sub 
out &msg # msg -> msg is not defined in sub
```
#### global scope
```
scp main
set _msg s "Hello World!" # set global var msg
out *msg # Hello World!

scp sub # change scope from main to sub 
out *msg # Hello World!
```

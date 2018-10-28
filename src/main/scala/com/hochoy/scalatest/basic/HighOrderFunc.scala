

object HighOrderFunc {

  def f_add = (a: Int, b: Int) => {
    a + b
  }

  def f(ff: (Int, Int) => Int,
        x: Int,
        y: Int
       ): Int = {
    ff(x, y)
  }

  def main(args: Array[String]) {
    val sum = f(f_add , 100, 200)
    println(sum)
    val s = f(f_add , 100, 200)
    println(s)
  }
}
package id.ac.polbeng.susisalina.sqliteexample
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class StudentModel (
    val nim: String,
    val name: String,
    val age: String
): Parcelable
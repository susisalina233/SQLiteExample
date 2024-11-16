package id.ac.polbeng.susisalina.sqliteexample
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import java.util.ArrayList

class StudentDBHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "FeedReader.db"
        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DBContract.UserEntry.TABLE_NAME + " (" +
                    DBContract.UserEntry.COLUMN_NIM + " TEXT PRIMARY KEY," +
        DBContract.UserEntry.COLUMN_NAME + " TEXT," +
        DBContract.UserEntry.COLUMN_AGE + " TEXT)"
        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DBContract.UserEntry.TABLE_NAME
    }
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int,
                           newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int,
                             newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
    @Throws(SQLiteConstraintException::class)
    fun createStudent(student: StudentModel): Long {
        // Gets the data repository in write mode
        val db = writableDatabase
        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBContract.UserEntry.COLUMN_NIM, student.nim)
        values.put(DBContract.UserEntry.COLUMN_NAME, student.name)
        values.put(DBContract.UserEntry.COLUMN_AGE, student.age)
        // Insert the new row, returning the primary key value of the new row
                return db.insert(DBContract.UserEntry.TABLE_NAME, null, values)
    }
    fun searchStudentByNIM(nim: String): ArrayList<StudentModel> {
        val students = ArrayList<StudentModel>()
        val db = writableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery("select * from " +
                    DBContract.UserEntry.TABLE_NAME + " WHERE " +
                    DBContract.UserEntry.COLUMN_NIM + "='" + nim + "'", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }
        var name: String
        var age: String
        with (cursor) {
            if (moveToNext()) {
                name =
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME))
                age =
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_AGE))
                students.add(StudentModel(nim, name, age))
            }
        }
        cursor.close()
        return students
    }
    fun searchStudentByName(name: String): ArrayList<StudentModel> {
        val students = ArrayList<StudentModel>()
        val db = writableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery("select * from " +
                    DBContract.UserEntry.TABLE_NAME + " WHERE " +
                    DBContract.UserEntry.COLUMN_NAME + " LIKE \"%"+name+"%\"", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }
        var nim: String
        var name: String
        var age: String
        with (cursor) {
            while (moveToNext()) {
                nim =
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NIM))
                name =
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME))
                age =
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_AGE))
                students.add(StudentModel(nim, name, age))
            }
        }
        cursor.close()
        return students
    }
    fun readStudents(): ArrayList<StudentModel> {
        val users = ArrayList<StudentModel>()
        val db = writableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery("select * from " +
                    DBContract.UserEntry.TABLE_NAME + " order by " +
                    DBContract.UserEntry.COLUMN_NIM, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }
        var nim: String
        var name: String
        var age: String
        with (cursor) {
            while (moveToNext()) {
                nim =
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NIM))
                name =
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_NAME))
                age =
                    cursor.getString(cursor.getColumnIndexOrThrow(DBContract.UserEntry.COLUMN_AGE))
                users.add(StudentModel(nim, name, age))
            }
        }
        cursor.close()
        return users
    }
    @Throws(SQLiteConstraintException::class)
    fun updateStudent(student: StudentModel): Int {
        // Gets the data repository in write mode
        val db = writableDatabase
        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBContract.UserEntry.COLUMN_NAME, student.name)
        values.put(DBContract.UserEntry.COLUMN_AGE, student.age)
        // Which row to update, based on the title
        val selection = DBContract.UserEntry.COLUMN_NIM + " LIKE ?"
        val selectionArgs = arrayOf(student.nim)
        return db.update(
            DBContract.UserEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )
    }
    @Throws(SQLiteConstraintException::class)
    fun deleteStudent(nim: String): Int {
        // Gets the data repository in write mode
        val db = writableDatabase
        // Define 'where' part of query.
        val selection = DBContract.UserEntry.COLUMN_NIM + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(nim)
        // Issue SQL statement.
        return db.delete(DBContract.UserEntry.TABLE_NAME, selection,
            selectionArgs)
    }
}

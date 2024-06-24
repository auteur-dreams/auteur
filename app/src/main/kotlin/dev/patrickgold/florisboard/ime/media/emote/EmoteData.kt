package dev.patrickgold.florisboard.ime.media.emote

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.TypeConverters
import com.google.firebase.firestore.DocumentSnapshot
import dev.patrickgold.florisboard.lib.util.Converters
import kotlinx.serialization.Serializable

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var categoryName: String,
    var imageDrawable: Int
)

@Serializable
@Entity(tableName = "emotes")
@TypeConverters(Converters::class) // Assuming Converters handles List<String> conversion
data class Emote(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var objectID: String = "", // Firestore's document id
    var name: String = "",
    var drawableName: String = "",
    var categories: List<String>? = null,
    var remoteUrl: String = "",
    var localPath: String = "",
    var isDownloaded: Boolean = false,
    var fileType: String = "",
    var type: Type = Type.STATIC
) {
    enum class Type {
        STATIC, ANIMATED
    }

    companion object {
        fun fromDocument(document: DocumentSnapshot): Emote {
            return Emote(
                objectID = document.id,
                name = document.getString("name") ?: "",
                drawableName = document.getString("drawableName") ?: "",
                categories = document.get("categories") as? List<String>,
                remoteUrl = document.getString("remoteUrl") ?: "",
                localPath = document.getString("localPath") ?: "",
                isDownloaded = document.getBoolean("isDownloaded") ?: false,
                fileType = document.getString("fileType") ?: "",
                type = when (document.getString("fileType")) {
                    "gif" -> Type.ANIMATED
                    else -> Type.STATIC
                }
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Emote

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + drawableName.hashCode()
        result = 31 * result + (categories?.hashCode() ?: 0)
        result = 31 * result + remoteUrl.hashCode()
        result = 31 * result + localPath.hashCode()
        result = 31 * result + isDownloaded.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}

@Entity(
    tableName = "emote_category_join",
    primaryKeys = ["emoteId", "categoryId"],
    foreignKeys = [
        ForeignKey(
            entity = Emote::class,
            parentColumns = ["id"],
            childColumns = ["emoteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["categoryId"])]
)
data class EmoteCategoryJoin(
    val emoteId: Int,
    val categoryId: Int
)

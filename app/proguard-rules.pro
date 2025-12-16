-printmapping mapping.txt
#-dontobfuscate
#-keepnames class com.path.to.your.ParcelableArg
#-keepnames class com.path.to.your.SerializableArg
#-keepnames class com.path.to.your.EnumArg

-keep class com.oudombun.chelfy.models.** { *; }

#-keepclassmembers class * {
#    @androidx.navigation.NavArgument *;
#}

# Keep DataStore generated classes
-keep class androidx.datastore.** { *; }

# Keep Preference keys (as they might be used with reflection)
-keep class com.oudombun.chelfy.**.PreferenceKeys { *; }

# Keep your MealAndDietType model
-keep class com.oudombun.chelfy.data.MealAndDietType { *; }

# Kotlin Coroutines Support
-keepclassmembers class kotlinx.coroutines.flow.** {
    public *;
}

# --- Keep Room components ---
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class * { @androidx.room.TypeConverter *; }
-keepclassmembers class * { @androidx.room.TypeConverter *; }

# --- Gson reflection: keep model fields/names ---
# (Adjust your package to match your models)
-keep class com.oudombun.chelfy.models.** { *; }

# If you use @SerializedName, keep those fields (optional but helpful)
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# --- Keep TypeToken/Signature info used by Gson ---
-keepattributes Signature
-keepattributes *Annotation*
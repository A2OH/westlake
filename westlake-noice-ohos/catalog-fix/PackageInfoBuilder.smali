.class public Ladapter/packagemanager/PackageInfoBuilder;
.super Ljava/lang/Object;
.source "PackageInfoBuilder.java"


# static fields
.field private static final TAG:Ljava/lang/String; = "OH_PkgInfoBuilder"


# direct methods
.method public constructor <init>()V
    .locals 0

    .line 36
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method private static buildActivityInfo(Lorg/json/JSONObject;Ljava/lang/String;)Landroid/content/pm/ActivityInfo;
    .locals 3
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lorg/json/JSONException;
        }
    .end annotation

    .line 206
    new-instance v0, Landroid/content/pm/ActivityInfo;

    invoke-direct {v0}, Landroid/content/pm/ActivityInfo;-><init>()V

    const-string v1, "name"

    .line 207
    invoke-virtual {p0, v1}, Lorg/json/JSONObject;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    .line 210
    invoke-static {p1, v1}, Ladapter/activity/IntentWantConverter;->abilityNameToClassName(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    iput-object v1, v0, Landroid/content/pm/ActivityInfo;->name:Ljava/lang/String;

    .line 211
    iput-object p1, v0, Landroid/content/pm/ActivityInfo;->packageName:Ljava/lang/String;

    const-string p1, "visible"

    const/4 v1, 0x0

    .line 212
    invoke-virtual {p0, p1, v1}, Lorg/json/JSONObject;->optBoolean(Ljava/lang/String;Z)Z

    move-result p1

    iput-boolean p1, v0, Landroid/content/pm/ActivityInfo;->exported:Z

    const-string p1, "launchMode"

    const-string v2, "STANDARD"

    .line 215
    invoke-virtual {p0, p1, v2}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object p1

    const-string v2, "SINGLETON"

    .line 216
    invoke-virtual {v2, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-eqz p1, :cond_0

    const/4 p1, 0x2

    .line 217
    iput p1, v0, Landroid/content/pm/ActivityInfo;->launchMode:I

    goto :goto_0

    .line 219
    :cond_0
    iput v1, v0, Landroid/content/pm/ActivityInfo;->launchMode:I

    :goto_0
    const-string p1, "orientation"

    const-string v2, "UNSPECIFIED"

    .line 223
    invoke-virtual {p0, p1, v2}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object p0

    .line 224
    invoke-virtual {p0}, Ljava/lang/String;->hashCode()I

    const-string p1, "LANDSCAPE"

    invoke-virtual {p0, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p1

    if-nez p1, :cond_2

    const-string p1, "PORTRAIT"

    invoke-virtual {p0, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result p0

    if-nez p0, :cond_1

    const/4 p0, -0x1

    .line 232
    iput p0, v0, Landroid/content/pm/ActivityInfo;->screenOrientation:I

    goto :goto_1

    :cond_1
    const/4 p0, 0x1

    .line 229
    iput p0, v0, Landroid/content/pm/ActivityInfo;->screenOrientation:I

    goto :goto_1

    .line 226
    :cond_2
    iput v1, v0, Landroid/content/pm/ActivityInfo;->screenOrientation:I

    :goto_1
    return-object v0
.end method

.method private static buildApplicationInfo(Lorg/json/JSONObject;Ljava/lang/String;)Landroid/content/pm/ApplicationInfo;
    .locals 8

    .line 119
    new-instance v0, Landroid/content/pm/ApplicationInfo;

    invoke-direct {v0}, Landroid/content/pm/ApplicationInfo;-><init>()V

    new-instance v1, Landroid/os/Bundle;

    invoke-direct {v1}, Landroid/os/Bundle;-><init>()V

    iput-object v1, v0, Landroid/content/pm/ApplicationInfo;->metaData:Landroid/os/Bundle;

    .line 120
    iput-object p1, v0, Landroid/content/pm/ApplicationInfo;->packageName:Ljava/lang/String;

    const-string v1, "applicationInfo"

    .line 122
    invoke-virtual {p0, v1}, Lorg/json/JSONObject;->optJSONObject(Ljava/lang/String;)Lorg/json/JSONObject;

    move-result-object v1

    if-nez v1, :cond_0

    .line 123
    new-instance v1, Lorg/json/JSONObject;

    invoke-direct {v1}, Lorg/json/JSONObject;-><init>()V

    :cond_0
    const-string v2, "process"

    const-string v3, ""

    .line 126
    invoke-virtual {v1, v2, v3}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    .line 127
    invoke-virtual {v2}, Ljava/lang/String;->isEmpty()Z

    move-result v4

    if-eqz v4, :cond_1

    move-object v2, p1

    :cond_1
    iput-object v2, v0, Landroid/content/pm/ApplicationInfo;->processName:Ljava/lang/String;

    const-string v2, "dataDir"

    .line 129
    invoke-virtual {v1, v2, v3}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    .line 130
    invoke-virtual {v2}, Ljava/lang/String;->isEmpty()Z

    move-result v4

    if-eqz v4, :cond_2

    new-instance v2, Ljava/lang/StringBuilder;

    const-string v4, "/data/data/"

    invoke-direct {v2, v4}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    :cond_2
    iput-object v2, v0, Landroid/content/pm/ApplicationInfo;->dataDir:Ljava/lang/String;

    .line 131
    iget-object v2, v0, Landroid/content/pm/ApplicationInfo;->dataDir:Ljava/lang/String;

    iput-object v2, v0, Landroid/content/pm/ApplicationInfo;->deviceProtectedDataDir:Ljava/lang/String;

    .line 132
    iget-object v2, v0, Landroid/content/pm/ApplicationInfo;->dataDir:Ljava/lang/String;

    iput-object v2, v0, Landroid/content/pm/ApplicationInfo;->credentialProtectedDataDir:Ljava/lang/String;

    const-string v2, "codePath"

    .line 134
    invoke-virtual {v1, v2, v3}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    .line 135
    invoke-virtual {v2}, Ljava/lang/String;->isEmpty()Z

    move-result v4

    const-string v5, "/"

    const-string v6, "/system/app/"

    const-string v7, ".apk"

    if-nez v4, :cond_3

    invoke-virtual {v2, v7}, Ljava/lang/String;->endsWith(Ljava/lang/String;)Z

    move-result v4

    if-nez v4, :cond_3

    .line 136
    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v4, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v4, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v4, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    iput-object v2, v0, Landroid/content/pm/ApplicationInfo;->sourceDir:Ljava/lang/String;

    goto :goto_0

    .line 137
    :cond_3
    invoke-virtual {v2}, Ljava/lang/String;->isEmpty()Z

    move-result v4

    if-nez v4, :cond_4

    .line 138
    iput-object v2, v0, Landroid/content/pm/ApplicationInfo;->sourceDir:Ljava/lang/String;

    goto :goto_0

    .line 140
    :cond_4
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2, v6}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    iput-object v2, v0, Landroid/content/pm/ApplicationInfo;->sourceDir:Ljava/lang/String;

    .line 142
    :goto_0
    iget-object v2, v0, Landroid/content/pm/ApplicationInfo;->sourceDir:Ljava/lang/String;

    iput-object v2, v0, Landroid/content/pm/ApplicationInfo;->publicSourceDir:Ljava/lang/String;

    const-string v2, "cpuAbi"

    .line 144
    invoke-virtual {v1, v2, v3}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    .line 145
    invoke-static {v2}, Ladapter/packagemanager/PackageInfoBuilder;->mapAbi(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    iput-object v2, v0, Landroid/content/pm/ApplicationInfo;->primaryCpuAbi:Ljava/lang/String;

    const-string v2, "nativeLibraryPath"

    .line 146
    invoke-virtual {v1, v2, v3}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    .line 147
    invoke-virtual {v2}, Ljava/lang/String;->isEmpty()Z

    move-result v3

    if-nez v3, :cond_5

    goto :goto_1

    .line 149
    :cond_5
    iget-object v2, v0, Landroid/content/pm/ApplicationInfo;->primaryCpuAbi:Ljava/lang/String;

    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3, v6}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v3, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v4, "/lib/"

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v3, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    :goto_1
    iput-object v2, v0, Landroid/content/pm/ApplicationInfo;->nativeLibraryDir:Ljava/lang/String;

    const/4 v2, -0x1

    const-string v3, "uid"

    .line 152
    invoke-virtual {p0, v3, v2}, Lorg/json/JSONObject;->optInt(Ljava/lang/String;I)I

    move-result v2

    invoke-virtual {v1, v3, v2}, Lorg/json/JSONObject;->optInt(Ljava/lang/String;I)I

    move-result v2

    .line 153
    iput v2, v0, Landroid/content/pm/ApplicationInfo;->uid:I

    const-string v2, "versionCode"

    const/4 v3, 0x0

    .line 156
    invoke-virtual {p0, v2, v3}, Lorg/json/JSONObject;->optInt(Ljava/lang/String;I)I

    move-result v2

    iput v2, v0, Landroid/content/pm/ApplicationInfo;->versionCode:I

    .line 157
    iget v2, v0, Landroid/content/pm/ApplicationInfo;->versionCode:I

    int-to-long v4, v2

    iput-wide v4, v0, Landroid/content/pm/ApplicationInfo;->longVersionCode:J

    const-string v2, "maxSdkVersion"

    const/16 v4, 0x22

    .line 158
    invoke-virtual {p0, v2, v4}, Lorg/json/JSONObject;->optInt(Ljava/lang/String;I)I

    move-result p0

    const-string v2, "apiTargetVersion"

    invoke-virtual {v1, v2, p0}, Lorg/json/JSONObject;->optInt(Ljava/lang/String;I)I

    move-result p0

    const/16 v2, 0x24

    const/16 v5, 0x15

    if-lt p0, v5, :cond_6

    if-gt p0, v2, :cond_6

    move v4, p0

    .line 159
    :cond_6
    iput v4, v0, Landroid/content/pm/ApplicationInfo;->targetSdkVersion:I

    const-string p0, "apiCompatibleVersion"

    const/16 v4, 0x18

    .line 160
    invoke-virtual {v1, p0, v4}, Lorg/json/JSONObject;->optInt(Ljava/lang/String;I)I

    move-result p0

    if-lt p0, v5, :cond_7

    if-gt p0, v2, :cond_7

    move v4, p0

    .line 161
    :cond_7
    iput v4, v0, Landroid/content/pm/ApplicationInfo;->minSdkVersion:I

    .line 162
    iget p0, v0, Landroid/content/pm/ApplicationInfo;->targetSdkVersion:I

    iput p0, v0, Landroid/content/pm/ApplicationInfo;->compileSdkVersion:I

    .line 171
    iput v3, v0, Landroid/content/pm/ApplicationInfo;->icon:I

    .line 172
    iput v3, v0, Landroid/content/pm/ApplicationInfo;->iconRes:I

    .line 173
    iput v3, v0, Landroid/content/pm/ApplicationInfo;->banner:I

    .line 174
    iput v3, v0, Landroid/content/pm/ApplicationInfo;->logo:I

    .line 175
    iput v3, v0, Landroid/content/pm/ApplicationInfo;->labelRes:I

    .line 176
    iput v3, v0, Landroid/content/pm/ApplicationInfo;->descriptionRes:I

    .line 177
    iput v3, v0, Landroid/content/pm/ApplicationInfo;->theme:I

    .line 178
    sget-object p0, Ljava/lang/System;->err:Ljava/io/PrintStream;

    new-instance v2, Ljava/lang/StringBuilder;

    const-string v4, "[G2.5-PIB] ApplicationInfo "

    invoke-direct {v2, v4}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string p1, " icon/iconRes/labelRes/theme zeroed"

    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object p1

    invoke-virtual {p0, p1}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    const-string p0, "debug"

    .line 185
    invoke-virtual {v1, p0, v3}, Lorg/json/JSONObject;->optBoolean(Ljava/lang/String;Z)Z

    move-result p0

    if-eqz p0, :cond_8

    const p0, 0x802046

    goto :goto_2

    :cond_8
    const p0, 0x802044

    :goto_2
    const-string p1, "systemApp"

    .line 186
    invoke-virtual {v1, p1, v3}, Lorg/json/JSONObject;->optBoolean(Ljava/lang/String;Z)Z

    move-result p1

    if-eqz p1, :cond_9

    or-int/lit8 p0, p0, 0x1

    .line 187
    :cond_9
    iput p0, v0, Landroid/content/pm/ApplicationInfo;->flags:I

    const-string p0, "enabled"

    const/4 p1, 0x1

    .line 188
    invoke-virtual {v1, p0, p1}, Lorg/json/JSONObject;->optBoolean(Ljava/lang/String;Z)Z

    move-result p0

    iput-boolean p0, v0, Landroid/content/pm/ApplicationInfo;->enabled:Z

    return-object v0
.end method

.method private static buildExtensionInfos(Lorg/json/JSONArray;Landroid/content/pm/PackageInfo;)V
    .locals 13
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lorg/json/JSONException;
        }
    .end annotation

    const/4 v0, 0x0

    const/4 v1, 0x0

    const/4 v2, 0x0

    const/4 v3, 0x0

    .line 245
    :goto_0
    invoke-virtual {p0}, Lorg/json/JSONArray;->length()I

    move-result v4

    const-string v5, "DATASHARE"

    const-string v6, "SERVICE"

    const-string v7, "type"

    const-string v8, ""

    if-ge v1, v4, :cond_2

    .line 246
    invoke-virtual {p0, v1}, Lorg/json/JSONArray;->getJSONObject(I)Lorg/json/JSONObject;

    move-result-object v4

    .line 247
    invoke-virtual {v4, v7, v8}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v4

    .line 248
    invoke-virtual {v6, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v6

    if-eqz v6, :cond_0

    add-int/lit8 v2, v2, 0x1

    goto :goto_1

    .line 250
    :cond_0
    invoke-virtual {v5, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_1

    add-int/lit8 v3, v3, 0x1

    :cond_1
    :goto_1
    add-int/lit8 v1, v1, 0x1

    goto :goto_0

    :cond_2
    const-string v1, "visible"

    const-string v4, "name"

    if-lez v2, :cond_4

    .line 257
    new-array v2, v2, [Landroid/content/pm/ServiceInfo;

    iput-object v2, p1, Landroid/content/pm/PackageInfo;->services:[Landroid/content/pm/ServiceInfo;

    const/4 v2, 0x0

    const/4 v9, 0x0

    .line 259
    :goto_2
    invoke-virtual {p0}, Lorg/json/JSONArray;->length()I

    move-result v10

    if-ge v2, v10, :cond_4

    .line 260
    invoke-virtual {p0, v2}, Lorg/json/JSONArray;->getJSONObject(I)Lorg/json/JSONObject;

    move-result-object v10

    .line 261
    invoke-virtual {v10, v7, v8}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v11

    invoke-virtual {v6, v11}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v11

    if-eqz v11, :cond_3

    .line 262
    new-instance v11, Landroid/content/pm/ServiceInfo;

    invoke-direct {v11}, Landroid/content/pm/ServiceInfo;-><init>()V

    .line 263
    invoke-virtual {v10, v4}, Lorg/json/JSONObject;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v12

    iput-object v12, v11, Landroid/content/pm/ServiceInfo;->name:Ljava/lang/String;

    .line 264
    iget-object v12, p1, Landroid/content/pm/PackageInfo;->packageName:Ljava/lang/String;

    iput-object v12, v11, Landroid/content/pm/ServiceInfo;->packageName:Ljava/lang/String;

    .line 265
    invoke-virtual {v10, v1, v0}, Lorg/json/JSONObject;->optBoolean(Ljava/lang/String;Z)Z

    move-result v10

    iput-boolean v10, v11, Landroid/content/pm/ServiceInfo;->exported:Z

    .line 266
    iget-object v10, p1, Landroid/content/pm/PackageInfo;->services:[Landroid/content/pm/ServiceInfo;

    add-int/lit8 v12, v9, 0x1

    aput-object v11, v10, v9

    move v9, v12

    :cond_3
    add-int/lit8 v2, v2, 0x1

    goto :goto_2

    :cond_4
    if-lez v3, :cond_7

    .line 272
    new-array v2, v3, [Landroid/content/pm/ProviderInfo;

    iput-object v2, p1, Landroid/content/pm/PackageInfo;->providers:[Landroid/content/pm/ProviderInfo;

    const/4 v2, 0x0

    const/4 v3, 0x0

    .line 274
    :goto_3
    invoke-virtual {p0}, Lorg/json/JSONArray;->length()I

    move-result v6

    if-ge v2, v6, :cond_7

    .line 275
    invoke-virtual {p0, v2}, Lorg/json/JSONArray;->getJSONObject(I)Lorg/json/JSONObject;

    move-result-object v6

    .line 276
    invoke-virtual {v6, v7, v8}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v9

    invoke-virtual {v5, v9}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v9

    if-eqz v9, :cond_6

    .line 277
    new-instance v9, Landroid/content/pm/ProviderInfo;

    invoke-direct {v9}, Landroid/content/pm/ProviderInfo;-><init>()V

    .line 278
    invoke-virtual {v6, v4}, Lorg/json/JSONObject;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v10

    iput-object v10, v9, Landroid/content/pm/ProviderInfo;->name:Ljava/lang/String;

    .line 279
    iget-object v10, p1, Landroid/content/pm/PackageInfo;->packageName:Ljava/lang/String;

    iput-object v10, v9, Landroid/content/pm/ProviderInfo;->packageName:Ljava/lang/String;

    .line 280
    invoke-virtual {v6, v1, v0}, Lorg/json/JSONObject;->optBoolean(Ljava/lang/String;Z)Z

    move-result v10

    iput-boolean v10, v9, Landroid/content/pm/ProviderInfo;->exported:Z

    const-string v10, "uri"

    .line 281
    invoke-virtual {v6, v10, v8}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v6

    const-string v10, "datashare:///"

    .line 282
    invoke-virtual {v6, v10}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v10

    if-eqz v10, :cond_5

    const/16 v10, 0xd

    .line 283
    invoke-virtual {v6, v10}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v6

    iput-object v6, v9, Landroid/content/pm/ProviderInfo;->authority:Ljava/lang/String;

    .line 285
    :cond_5
    iget-object v6, p1, Landroid/content/pm/PackageInfo;->providers:[Landroid/content/pm/ProviderInfo;

    add-int/lit8 v10, v3, 0x1

    aput-object v9, v6, v3

    move v3, v10

    :cond_6
    add-int/lit8 v2, v2, 0x1

    goto :goto_3

    :cond_7
    return-void
.end method

.method public static fromBundleInfo(Ljava/lang/String;)Landroid/content/pm/PackageInfo;
    .locals 3

    .line 56
    new-instance v0, Landroid/content/pm/PackageInfo;

    invoke-direct {v0}, Landroid/content/pm/PackageInfo;-><init>()V

    const-string v1, "OH_PkgInfoBuilder"

    if-eqz p0, :cond_1

    .line 58
    invoke-virtual {p0}, Ljava/lang/String;->isEmpty()Z

    move-result v2

    if-eqz v2, :cond_0

    goto :goto_0

    .line 64
    :cond_0
    :try_start_0
    new-instance v2, Lorg/json/JSONObject;

    invoke-direct {v2, p0}, Lorg/json/JSONObject;-><init>(Ljava/lang/String;)V

    .line 65
    invoke-static {v2}, Ladapter/packagemanager/PackageInfoBuilder;->fromBundleInfoJson(Lorg/json/JSONObject;)Landroid/content/pm/PackageInfo;

    move-result-object p0
    :try_end_0
    .catch Lorg/json/JSONException; {:try_start_0 .. :try_end_0} :catch_0

    return-object p0

    :catch_0
    move-exception p0

    const-string v2, "Failed to parse BundleInfo JSON"

    .line 67
    invoke-static {v1, v2, p0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    return-object v0

    :cond_1
    :goto_0
    const-string p0, "Empty bundleInfoJson"

    .line 59
    invoke-static {v1, p0}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    return-object v0
.end method

.method public static fromBundleInfoJson(Lorg/json/JSONObject;)Landroid/content/pm/PackageInfo;
    .locals 7
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lorg/json/JSONException;
        }
    .end annotation

    .line 76
    new-instance v0, Landroid/content/pm/PackageInfo;

    invoke-direct {v0}, Landroid/content/pm/PackageInfo;-><init>()V

    const-string v1, "name"

    .line 79
    invoke-virtual {p0, v1}, Lorg/json/JSONObject;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    iput-object v1, v0, Landroid/content/pm/PackageInfo;->packageName:Ljava/lang/String;

    const-string v1, "versionCode"

    const/4 v2, 0x0

    .line 80
    invoke-virtual {p0, v1, v2}, Lorg/json/JSONObject;->optInt(Ljava/lang/String;I)I

    move-result v1

    iput v1, v0, Landroid/content/pm/PackageInfo;->versionCode:I

    const-string v1, "versionName"

    const-string v3, ""

    .line 81
    invoke-virtual {p0, v1, v3}, Lorg/json/JSONObject;->optString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    iput-object v1, v0, Landroid/content/pm/PackageInfo;->versionName:Ljava/lang/String;

    .line 84
    iget-object v1, v0, Landroid/content/pm/PackageInfo;->packageName:Ljava/lang/String;

    invoke-static {p0, v1}, Ladapter/packagemanager/PackageInfoBuilder;->buildApplicationInfo(Lorg/json/JSONObject;Ljava/lang/String;)Landroid/content/pm/ApplicationInfo;

    move-result-object v1

    iput-object v1, v0, Landroid/content/pm/PackageInfo;->applicationInfo:Landroid/content/pm/ApplicationInfo;

    const-string v1, "abilityInfos"

    .line 87
    invoke-virtual {p0, v1}, Lorg/json/JSONObject;->optJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v1

    if-eqz v1, :cond_0

    .line 88
    invoke-virtual {v1}, Lorg/json/JSONArray;->length()I

    move-result v3

    if-lez v3, :cond_0

    .line 89
    invoke-virtual {v1}, Lorg/json/JSONArray;->length()I

    move-result v3

    new-array v3, v3, [Landroid/content/pm/ActivityInfo;

    iput-object v3, v0, Landroid/content/pm/PackageInfo;->activities:[Landroid/content/pm/ActivityInfo;

    const/4 v3, 0x0

    .line 90
    :goto_0
    invoke-virtual {v1}, Lorg/json/JSONArray;->length()I

    move-result v4

    if-ge v3, v4, :cond_0

    .line 91
    iget-object v4, v0, Landroid/content/pm/PackageInfo;->activities:[Landroid/content/pm/ActivityInfo;

    .line 92
    invoke-virtual {v1, v3}, Lorg/json/JSONArray;->getJSONObject(I)Lorg/json/JSONObject;

    move-result-object v5

    iget-object v6, v0, Landroid/content/pm/PackageInfo;->packageName:Ljava/lang/String;

    .line 91
    invoke-static {v5, v6}, Ladapter/packagemanager/PackageInfoBuilder;->buildActivityInfo(Lorg/json/JSONObject;Ljava/lang/String;)Landroid/content/pm/ActivityInfo;

    move-result-object v5

    aput-object v5, v4, v3

    add-int/lit8 v3, v3, 0x1

    goto :goto_0

    :cond_0
    const-string v1, "extensionAbilityInfos"

    .line 97
    invoke-virtual {p0, v1}, Lorg/json/JSONObject;->optJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object v1

    if-eqz v1, :cond_1

    .line 98
    invoke-virtual {v1}, Lorg/json/JSONArray;->length()I

    move-result v3

    if-lez v3, :cond_1

    .line 99
    invoke-static {v1, v0}, Ladapter/packagemanager/PackageInfoBuilder;->buildExtensionInfos(Lorg/json/JSONArray;Landroid/content/pm/PackageInfo;)V

    :cond_1
    const-string v1, "reqPermissions"

    .line 103
    invoke-virtual {p0, v1}, Lorg/json/JSONObject;->optJSONArray(Ljava/lang/String;)Lorg/json/JSONArray;

    move-result-object p0

    if-eqz p0, :cond_2

    .line 104
    invoke-virtual {p0}, Lorg/json/JSONArray;->length()I

    move-result v1

    if-lez v1, :cond_2

    .line 105
    invoke-virtual {p0}, Lorg/json/JSONArray;->length()I

    move-result v1

    new-array v1, v1, [Ljava/lang/String;

    iput-object v1, v0, Landroid/content/pm/PackageInfo;->requestedPermissions:[Ljava/lang/String;

    .line 106
    :goto_1
    invoke-virtual {p0}, Lorg/json/JSONArray;->length()I

    move-result v1

    if-ge v2, v1, :cond_2

    .line 107
    iget-object v1, v0, Landroid/content/pm/PackageInfo;->requestedPermissions:[Ljava/lang/String;

    .line 108
    invoke-virtual {p0, v2}, Lorg/json/JSONArray;->getString(I)Ljava/lang/String;

    move-result-object v3

    invoke-static {v3}, Ladapter/packagemanager/PermissionMapper;->mapToAndroid(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    aput-object v3, v1, v2

    add-int/lit8 v2, v2, 0x1

    goto :goto_1

    :cond_2
    return-object v0
.end method

.method private static mapAbi(Ljava/lang/String;)Ljava/lang/String;
    .locals 6

    const-string v0, "armeabi-v7a"

    if-eqz p0, :cond_8

    .line 194
    invoke-virtual {p0}, Ljava/lang/String;->isEmpty()Z

    move-result v1

    if-eqz v1, :cond_0

    goto/16 :goto_1

    .line 195
    :cond_0
    invoke-virtual {p0}, Ljava/lang/String;->hashCode()I

    invoke-virtual {p0}, Ljava/lang/String;->hashCode()I

    move-result v1

    const-string v2, "arm64-v8a"

    const-string v3, "x86"

    const-string v4, "x86_64"

    const/4 v5, -0x1

    sparse-switch v1, :sswitch_data_0

    goto :goto_0

    :sswitch_0
    invoke-virtual {p0, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_1

    goto :goto_0

    :cond_1
    const/4 v5, 0x6

    goto :goto_0

    :sswitch_1
    invoke-virtual {p0, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_2

    goto :goto_0

    :cond_2
    const/4 v5, 0x5

    goto :goto_0

    :sswitch_2
    const-string v1, "arm64"

    invoke-virtual {p0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_3

    goto :goto_0

    :cond_3
    const/4 v5, 0x4

    goto :goto_0

    :sswitch_3
    invoke-virtual {p0, v3}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_4

    goto :goto_0

    :cond_4
    const/4 v5, 0x3

    goto :goto_0

    :sswitch_4
    const-string v1, "arm"

    invoke-virtual {p0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_5

    goto :goto_0

    :cond_5
    const/4 v5, 0x2

    goto :goto_0

    :sswitch_5
    const-string v1, "armeabi"

    invoke-virtual {p0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_6

    goto :goto_0

    :cond_6
    const/4 v5, 0x1

    goto :goto_0

    :sswitch_6
    invoke-virtual {p0, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_7

    goto :goto_0

    :cond_7
    const/4 v5, 0x0

    :goto_0
    packed-switch v5, :pswitch_data_0

    return-object p0

    :pswitch_0
    return-object v2

    :pswitch_1
    return-object v3

    :pswitch_2
    return-object v0

    :pswitch_3
    return-object v4

    :cond_8
    :goto_1
    return-object v0

    nop

    :sswitch_data_0
    .sparse-switch
        -0x300b59d9 -> :sswitch_6
        -0x2c0bb1c1 -> :sswitch_5
        0x17a5c -> :sswitch_4
        0x1c976 -> :sswitch_3
        0x58c5a1a -> :sswitch_2
        0x8ab4d72 -> :sswitch_1
        0x5553f3ec -> :sswitch_0
    .end sparse-switch

    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_3
        :pswitch_2
        :pswitch_2
        :pswitch_1
        :pswitch_0
        :pswitch_2
        :pswitch_0
    .end packed-switch
.end method

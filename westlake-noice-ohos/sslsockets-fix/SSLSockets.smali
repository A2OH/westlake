.class public Landroid/net/ssl/SSLSockets;
.super Ljava/lang/Object;
.source "SSLSockets.java"


# direct methods
.method private constructor <init>()V
    .registers 1

    .line 4
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static isSupportedSocket(Ljavax/net/ssl/SSLSocket;)Z
    .registers 1

    .line 5
    const/4 p0, 0x0

    return p0
.end method

.method public static setUseSessionTickets(Ljavax/net/ssl/SSLSocket;Z)V
    .registers 2

    .line 6
    return-void
.end method

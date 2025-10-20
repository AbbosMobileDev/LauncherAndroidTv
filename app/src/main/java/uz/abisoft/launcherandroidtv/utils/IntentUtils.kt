package uz.abisoft.launcherandroidtv.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast

fun launchApp(ctx: Context, pkg: String, cls: String): Boolean =
    try {
        ctx.startActivity(Intent().setClassName(pkg, cls).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        true
    } catch (e: Exception) {
        Toast.makeText(ctx, "Ishga tushmadi: ${e.message}", Toast.LENGTH_SHORT).show()
        false
    }

package com.example.linkpreview

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.URLUtil
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.linkpreview.ui.theme.LinkPreview
import com.example.linkpreview.ui.theme.LinkPreviewTheme
import com.example.linkpreview.ui.theme.cardBackground
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LinkPreviewTheme {
                var searchContent by remember { mutableStateOf("") }
                var linkPreview by remember { mutableStateOf<LinkPreview?>(null) }
                Column(modifier = Modifier.fillMaxHeight()) {
                    Spacer(modifier = Modifier.weight(1f))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        linkPreview?.let { PreviewLinkCard(linkPreview = it) }
                        RoundedTextFieldWithSendIcon(
                            text = searchContent,
                            onTextChanged = { newText ->
                                searchContent = newText
                                if (newText.isBlank()) {
                                    linkPreview = null
                                }
                            },
                            onSendClicked = {
                            }
                        )
                    }
                    LaunchedEffect(searchContent) {
                        linkPreview = if (searchContent.isNotBlank() && URLUtil.isValidUrl(searchContent)) {
                            val linkPv = fetchLinkPreview(searchContent)
                            linkPv
                        } else {
                            null
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewLinkCard(linkPreview: LinkPreview) {
    val context = LocalContext.current as Activity
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(cardBackground),
        onClick = {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(linkPreview.url)))
        }
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            linkPreview.img?.let {
                Image(
                    painter = rememberAsyncImagePainter(model = it),
                    contentDescription = null,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
            Text(
                text = linkPreview.title.orEmpty(),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Text(
                text = linkPreview.description.orEmpty(),
                maxLines = 2
            )
        }
    }
}


suspend fun fetchLinkPreview(url: String): LinkPreview = withContext(Dispatchers.IO) {
    try {
        val response = Jsoup.connect(url).execute()
        val docs = response.parse().getElementsByTag("meta")
        val linkPv = LinkPreview(
            img = docs.firstOrNull { it.attr("property") == "og:image" }?.attr("content") ?: "",
            title = docs.firstOrNull { it.attr("property") == "og:title" }?.attr("content") ?: "",
            description = docs.firstOrNull { it.attr("property") == "og:description" }?.attr("content") ?: "",
            url = docs.firstOrNull { it.attr("property") == "og:url" }?.attr("content") ?: ""
        )
        if (linkPv.url?.isEmpty() == true) {
            throw Exception("Link preview not found")
        }
        linkPv
    } catch (e: Exception) {
        e.printStackTrace()
        LinkPreview()
    }
}

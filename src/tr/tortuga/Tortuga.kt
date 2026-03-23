package eu.kanade.tachiyomi.extension.tr.tortuga

import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.source.model.*
import okhttp3.Request
import okhttp3.Response

class Tortuga : ParsedHttpSource() {

    override val name = "Tortuga Çeviri"
    override val baseUrl = "https://tortugaceviri.com"
    override val lang = "tr"
    override val supportsLatest = true

    // 🔥 POPÜLER MANGA
    override fun popularMangaRequest(page: Int): Request {
        return GET("$baseUrl/manga", headers)
    }

    override fun popularMangaSelector() = "div.bsx"

    override fun popularMangaFromElement(element: org.jsoup.nodes.Element): SManga {
        return SManga.create().apply {
            title = element.select("div.tt").text()
            url = element.select("a").attr("href")
            thumbnail_url = element.select("img").attr("src")
        }
    }

    override fun popularMangaNextPageSelector() = "a.next"

    // 🔎 ARAMA
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        return GET("$baseUrl/?s=$query", headers)
    }

    override fun searchMangaSelector() = "div.bsx"

    override fun searchMangaFromElement(element: org.jsoup.nodes.Element) =
        popularMangaFromElement(element)

    override fun searchMangaNextPageSelector() = "a.next"

    // 📄 MANGA DETAY
    override fun mangaDetailsParse(document: org.jsoup.nodes.Document): SManga {
        return SManga.create().apply {
            title = document.select("h1").text()
            thumbnail_url = document.select("div.thumb img").attr("src")
            description = document.select("div.desc").text()
        }
    }

    // 📚 BÖLÜMLER
    override fun chapterListSelector() = "div.eplister ul li"

    override fun chapterFromElement(element: org.jsoup.nodes.Element): SChapter {
        return SChapter.create().apply {
            name = element.select("span.chapternum").text()
            url = element.select("a").attr("href")
        }
    }

    override fun chapterListParse(response: Response): List<SChapter> {
        return super.chapterListParse(response).reversed()
    }

    // 🖼️ SAYFALAR
    override fun pageListParse(document: org.jsoup.nodes.Document): List<Page> {
        val pages = mutableListOf<Page>()
        val images = document.select("div#readerarea img")

        for ((i, img) in images.withIndex()) {
            val url = img.attr("src")
            pages.add(Page(i, "", url))
        }

        return pages
    }

    override fun imageUrlParse(document: org.jsoup.nodes.Document): String {
        throw UnsupportedOperationException("Not used")
    }
}

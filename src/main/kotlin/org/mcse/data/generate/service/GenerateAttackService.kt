package org.mcse.data.generate.service

import jakarta.annotation.PostConstruct
import org.mcse.data.generate.models.DateProgression
import org.mcse.data.generate.models.attack.AttackVector
import org.mcse.data.generate.models.attack.AttackingClient
import org.mcse.data.generate.models.attack.RequestContent
import org.mcse.data.generate.models.attack.UpstreamContent
import org.mcse.data.generate.models.doa.Connection
import org.mcse.data.generate.models.doa.Entry
import org.mcse.data.generate.models.doa.Request
import org.mcse.data.generate.models.doa.Response
import org.mcse.data.generate.models.doa.Upstream
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class GenerateAttackService(
    private val entryDomainService: EntryDomainService,
) {

    companion object {

        //TOP 10 USER AGENTS FROM THE LOGS
        private val TOP_TEN_USER_AGENTS = listOf(
            "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Mobile Safari/537.36",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 17_6_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.6 Mobile/15E148 Safari/604.1",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/26.0 Chrome/122.0.0.0 Mobile Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.6 Safari/605.1.15",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Mobile Safari/537.36",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 18_0_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.0.1 Mobile/15E148 Safari/604.1",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36 Edg/129.0.0.0",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 17_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/129.0.6668.69 Mobile/15E148 Safari/604.1",
        )

        //as data mined from the logs for this specific attack
        private val REFERRER_OPTIONS = listOf("https://www.ekomenu.nl/", "https://www.ekomenu.be/")

        //as data mined from the logs
        private val PROTOCOL_OPTIONS = listOf("HTTP/1.0", "HTTP/1.1", "HTTP/2.0")

        //RECIPEBFF attack settings
        private val RECIPEBFF_MIN_ATTACK_REQUEST_INTERVAL_NANO = 250000000
        private val RECIPEBFF_MAX_ATTACK_REQUEST_INTERVAL_NANO = 750000000
        private val RECIPEBFF_MAX_ATTACK_CALLS = 1000

        private val RECIPEBFF_ATTACK_START_TRAINING = LocalDateTime.parse("2024-10-02T11:41:47")
        private val RECIPEBFF_ATTACK_START_VALIDATION = LocalDateTime.parse("2024-10-14T13:06:48")

        //Recipe id range that seems to make sense based upon the data.
        private val RECIPE_ID_RANGE = 10000..1000000

        //make the dates make sense for the period of logging. No real strange outliers.
        private val MIN_RECIPE_DATE = LocalDate.parse("2024-07-01")
        private val MAX_RECIPE_DATE = LocalDate.parse("2024-12-31")

        //SITEMAP attack settings
        private val SITEMAP_MIN_ATTACK_REQUEST_INTERVAL_NANO = 5000000000
        private val SITEMAP_MAX_ATTACK_REQUEST_INTERVAL_NANO = 7000000000
        private val SITEMAP_MIN_ATTACK_RESPONSE_MSEC = 17000
        private val SITEMAP_MAX_ATTACK_RESPONSE_MSEC = 22000
        private val SITEMAP_MAX_ATTACK_CALLS = 1000

        private val SITEMAP_ATTACK_START_TRAINING = LocalDateTime.parse("2024-09-28T21:03:39")
        private val SITEMAP_ATTACK_START_VALIDATION = LocalDateTime.parse("2024-10-21T05:21:56")

        //GENERIC SETTINGS
        private const val ATTACK_CLIENT = 500
        private val ZONE_OFFSET = ZoneId.of("Europe/Amsterdam").rules.getOffset(Instant.now())

        //Mined data 500.000 calls total 1000 * 500
        private val CALL_VECTORS_RECIPEBFF_ATTACK = listOf(
            AttackVector(
                uri = "/redirect/v1/redirect/url",
                formatUri = false,
                method = "POST",
                setupPath = true,
                //Mined from the data for this specific call
                contentLength = 15..149
            ),
            AttackVector(
                uri = "/recipebff/v1/recipe/list?ids=%d&from=%s&to=%s",
                formatUri = true,
                method = "GET",
                setupPath = false,
                //Mined from the data for this specific call
                contentLength = 17..1536
            ),
            AttackVector(
                uri = "/customerrating/v1/aggregated?originentitytype=RECIPE&originentityid=%d",
                formatUri = true,
                method = "GET",
                setupPath = false,
                //Mined from the data for this specific call
                contentLength = 17..829
            ),
        )

        //500.000 calls in total 1000 * 500
        private val CALL_VECTORS_ATTACK_SITEMAP = listOf(
            AttackVector(
                uri = "/redirect/v1/xmlsitemap?tld=be",
                formatUri = false,
                method = "GET",
                setupPath = false,
                //Mined from the data for this specific call
                contentLength = 56..56
            ),
        )
    }

    private val recipeBFFPossibleDatesRequest = mutableListOf<LocalDate>()

    @PostConstruct
    fun init() {
        DateProgression(MIN_RECIPE_DATE, MAX_RECIPE_DATE).forEach { date ->
            recipeBFFPossibleDatesRequest.add(date)
        }
    }

    fun generateAttack() {
        generateRecipeBFFAttack(false)
        println("RecipeBFF training attack done")
        generateRecipeBFFAttack(true)
        println("RecipeBFF validation attack done")
        generateXMLSiteMapAttack(false)
        println("XmlSiteMap training attack done")
        generateXMLSiteMapAttack(true)
        println("XmlSiteMap validation attack done")
    }

    private fun generateXMLSiteMapAttack(validation: Boolean) {
        val clients = generateClients()
        val attackStartDateTime = if(!validation) SITEMAP_ATTACK_START_TRAINING else SITEMAP_ATTACK_START_VALIDATION
        clients.forEach { client ->
            //spread clients start over 15 minutes to prevent spike in logs triggering an alarm
            val dateTimeStart = attackStartDateTime.plusMinutes(randomLongFromRange(0L..15L))
            makeSiteMapClientLogEntries(client, validation, dateTimeStart)
        }
    }

    private fun generateRecipeBFFAttack(validation: Boolean) {
        val clients = generateClients()
        val attackStartDateTime = if(!validation) RECIPEBFF_ATTACK_START_TRAINING else RECIPEBFF_ATTACK_START_VALIDATION
        clients.forEach { client ->
            //spread clients start over 15 minutes to prevent spike in logs triggering an alarm
            val dateTimeStart = attackStartDateTime.plusMinutes(randomLongFromRange(0L..15L))
            makeRecipeBFFClientLogEntries(client, validation, dateTimeStart)
        }
    }

    private fun makeSiteMapClientLogEntries(client: AttackingClient, validation: Boolean, dateTimeStart: LocalDateTime) {
        var dateTimeCurrentEntry = dateTimeStart
        val entries = mutableListOf<Entry>()
        (1..SITEMAP_MAX_ATTACK_CALLS).forEach { _ ->
            entries.add(makeSiteMapClientLogEntry(client, validation, dateTimeCurrentEntry))
            dateTimeCurrentEntry = siteMapncreaseCallDateTime(dateTimeCurrentEntry)
        }
        entries.forEach { entry ->
            entryDomainService.saveEntity(entry)
        }
    }

    private fun makeSiteMapClientLogEntry(
        client: AttackingClient,
        validation: Boolean,
        dateTimeCurrentEntry: LocalDateTime,
    ): Entry {
        return makeSiteMapLogEntry(client, dateTimeCurrentEntry, CALL_VECTORS_ATTACK_SITEMAP.first(), validation)
    }

    private fun makeSiteMapLogEntry(client: AttackingClient, dateTimeCurrent: LocalDateTime, vector: AttackVector, validation: Boolean): Entry {
        val connection = Connection(
            remoteIp = client.ip,
            remotePort = client.port,
            connectionId = client.connecttionId.toString(),
            connectionTime = siteMapCalculateConnectionTime(validation, dateTimeCurrent)
        )

        val requestContent = makeSiteMapRequestData(vector)
        val request = Request(
            authenticated = false,
            requestLength = requestContent.length,
            requestContentLength = requestContent.contentLength,
            requestContentType = "application/json",
            requestMethod = vector.method,
            requestUri = requestContent.uri,
            referrer = "",
            protocol = client.protocol,
            userAgent = client.userAgent,
        )

        val upstreamContent = makeSiteMapUpstreamDataListFunction()
        val upstream = Upstream(
            upstreamResponseTime = upstreamContent.upstreamResponseTime,
            upstreamResponseLength = upstreamContent.upstreamResponseLength,
            upstreamStatus = upstreamContent.upstreamStatus,
            upstreamConnectionTime = upstreamContent.upstreamResponseTime,
        )

        val response = Response(
            responseBodySize = upstreamContent.upstreamResponseLength,
            responseTotalSize = upstreamContent.upstreamResponseLength,
            responseStatus = upstreamContent.upstreamStatus,
            responseTime = upstreamContent.upstreamResponseTime!!
        )

        return Entry(
            date = dateTimeCurrent,
            time = (dateTimeCurrent.toInstant(ZONE_OFFSET).toEpochMilli().toDouble().div(1000.toDouble())).toBigDecimal(),
            malicious = true,
            validation = validation,
            connection = connection,
            upstream = upstream,
            response = response,
            request = request
        )
    }

    private fun makeRecipeBFFClientLogEntries(client: AttackingClient, validation: Boolean, dateTimeStart: LocalDateTime) {
        var dateTimeCurrentEntry = dateTimeStart
        var initializePath = true
        val entries = mutableListOf<Entry>()
        (1..RECIPEBFF_MAX_ATTACK_CALLS).forEach { callNumber ->
            entries.add(makeRecipeBFFClientLogEntry(client, validation, dateTimeCurrentEntry, initializePath, callNumber))
            dateTimeCurrentEntry = recipeBFFIncreaseCallDateTime(dateTimeCurrentEntry)
            initializePath = false
        }
        entries.forEach { entry ->
            entryDomainService.saveEntity(entry)
        }
    }

    private fun makeRecipeBFFClientLogEntry(
        client: AttackingClient,
        validation: Boolean,
        dateTimeCurrentEntry: LocalDateTime,
        initializePath: Boolean,
        callNumber: Int,
    ): Entry {
        return if(initializePath) {
            makeRecipeBFFClientLogEntrySetupPath(client, validation, dateTimeCurrentEntry)
        } else {
            makeRecipeBFFClientLogEntryAttack(client, validation, dateTimeCurrentEntry, callNumber)
        }
    }

    private fun makeRecipeBFFClientLogEntryAttack(client: AttackingClient, validation: Boolean, dateTimeCurrentEntry: LocalDateTime, callNumber: Int): Entry {
        val filtered = CALL_VECTORS_RECIPEBFF_ATTACK.filter { it.setupPath.not() }
        //Every 8th call add a customer rating
        if(callNumber % 8 == 0) {
            filtered[1].let { vector ->
                return makeRecipeBFFLogEntry(client, dateTimeCurrentEntry, vector, validation)
            }
        } else {
            filtered.first().let { vector ->
                return makeRecipeBFFLogEntry(client, dateTimeCurrentEntry, vector, validation)
            }
        }
    }

    private fun makeRecipeBFFClientLogEntrySetupPath(client: AttackingClient, validation: Boolean, dateTimeCurrentEntry: LocalDateTime): Entry {
        CALL_VECTORS_RECIPEBFF_ATTACK.first { it.setupPath }.let { vector ->
            return makeRecipeBFFLogEntry(client, dateTimeCurrentEntry, vector, validation)
        }
    }

    private fun makeRecipeBFFLogEntry(client: AttackingClient, dateTimeCurrent: LocalDateTime, vector: AttackVector, validation: Boolean): Entry {
        val connection = Connection(
            remoteIp = client.ip,
            remotePort = client.port,
            connectionId = client.connecttionId.toString(),
            connectionTime = recipeBFFCalculateConnectionTime(validation, dateTimeCurrent)
        )

        val requestContent = makeRecipeBFFRequestData(vector)
        val request = Request(
            authenticated = false,
            requestLength = requestContent.length,
            requestContentLength = requestContent.contentLength,
            requestContentType = "application/json",
            requestMethod = vector.method,
            requestUri = requestContent.uri,
            referrer = REFERRER_OPTIONS[randomIntFromRange(0..1)],
            protocol = client.protocol,
            userAgent = client.userAgent,
        )

        val upstreamContent = makeRecipeBFFUpstreamDataListFunction()
        val upstream = Upstream(
            upstreamResponseTime = upstreamContent.upstreamResponseTime,
            upstreamResponseLength = upstreamContent.upstreamResponseLength,
            upstreamStatus = upstreamContent.upstreamStatus,
            upstreamConnectionTime = upstreamContent.upstreamResponseTime,
        )

        val response = Response(
            responseBodySize = upstreamContent.upstreamResponseLength,
            responseTotalSize = upstreamContent.upstreamResponseLength,
            responseStatus = upstreamContent.upstreamStatus,
            responseTime = upstreamContent.upstreamResponseTime!!
        )

        return Entry(
            date = dateTimeCurrent,
            time = (dateTimeCurrent.toInstant(ZONE_OFFSET).toEpochMilli().toDouble().div(1000.toDouble())).toBigDecimal(),
            malicious = true,
            validation = validation,
            connection = connection,
            upstream = upstream,
            response = response,
            request = request
        )
    }

    private fun makeSiteMapRequestData(vector: AttackVector): RequestContent {
        return RequestContent(
                uri = vector.uri,
                length = vector.uri.length,
                contentLength = if(vector.contentLength != null) randomIntFromRange(vector.contentLength) else 0
            )
    }

    private fun makeSiteMapUpstreamDataListFunction(): UpstreamContent {
        //data mined from logs. Min is set to 750 milisec based upon less than average
        return UpstreamContent(
            upstreamResponseTime = (randomIntFromRange(SITEMAP_MIN_ATTACK_RESPONSE_MSEC..SITEMAP_MAX_ATTACK_RESPONSE_MSEC).toDouble().div(1000.toDouble())).toBigDecimal(),
            upstreamResponseLength = randomIntFromRange(1039202..1832994),
            upstreamStatus = 200,
        )
    }

    private fun makeRecipeBFFUpstreamDataListFunction(): UpstreamContent {
        //data mined from logs. Min is set to 750 milisec based upon less than average
        return UpstreamContent(
            upstreamResponseTime = (randomIntFromRange(750..1250).toDouble().div(1000.toDouble())).toBigDecimal(),
            upstreamResponseLength = randomIntFromRange(1080..1964416),
            upstreamStatus = 200,
        )
    }

    private fun makeRecipeBFFRequestData(vector: AttackVector): RequestContent {
        return if(!vector.formatUri) {
            RequestContent(
                uri = vector.uri,
                length = vector.uri.length,
                contentLength = if(vector.contentLength != null) randomIntFromRange(vector.contentLength) else 0
            )
        } else {
            val generatedRecipeId = randomIntFromRange(RECIPE_ID_RANGE)
            val dateRequest = randomRecipeBFFDate()
            val formatted = String.format(vector.uri, generatedRecipeId, dateRequest.toString(), dateRequest.toString())
            RequestContent(
                uri = formatted,
                length = formatted.length,
            )
        }
    }

    private fun recipeBFFCalculateConnectionTime(validation: Boolean, dateTimeCurrentEntry: LocalDateTime): BigDecimal {
        val startInstance = if(validation) {
            RECIPEBFF_ATTACK_START_VALIDATION.toInstant(ZONE_OFFSET)
        } else RECIPEBFF_ATTACK_START_TRAINING.toInstant(ZONE_OFFSET)
        val currentInstant = dateTimeCurrentEntry.toInstant(ZONE_OFFSET)
        return (currentInstant.toEpochMilli().minus(startInstance.toEpochMilli()).toDouble()).div(1000.toDouble()).toBigDecimal()
    }

    private fun siteMapCalculateConnectionTime(validation: Boolean, dateTimeCurrentEntry: LocalDateTime): BigDecimal {
        val startInstance = if(validation) {
            SITEMAP_ATTACK_START_VALIDATION.toInstant(ZONE_OFFSET)
        } else SITEMAP_ATTACK_START_TRAINING.toInstant(ZONE_OFFSET)
        val currentInstant = dateTimeCurrentEntry.toInstant(ZONE_OFFSET)
        return (currentInstant.toEpochMilli().minus(startInstance.toEpochMilli()).toDouble()).div(1000.toDouble()).toBigDecimal()
    }

    private fun generateClients(): MutableList<AttackingClient> {
        val attackClients = mutableListOf<AttackingClient>()
        (1..ATTACK_CLIENT).forEach { _ ->
            //Note there are ranges that are not actually allowed. We accept that these might be generated
            val randomIp = generateRandomIp()
            //not in well known ranges. In logs range is 545..65535
            val randomPort = randomIntFromRange(1025..65535)
            //log show range in the 689078953..952723283 but the range is not completely filled
            val randomConnectionId = randomLongFromRange(952723284L..2000000000L)
            val randomUserAgent = TOP_TEN_USER_AGENTS[randomIntFromRange(0..9)]
            val randomProtocol = PROTOCOL_OPTIONS[randomIntFromRange(0..2)]
            attackClients.add(
                AttackingClient(
                    ip = randomIp,
                    port = randomPort,
                    connecttionId = randomConnectionId,
                    userAgent = randomUserAgent,
                    protocol = randomProtocol,
                )
            )
        }
        return attackClients
    }

    private fun siteMapncreaseCallDateTime(dateTimeCurrentEntry: LocalDateTime): LocalDateTime {
        val addNano = randomLongFromRange(SITEMAP_MIN_ATTACK_REQUEST_INTERVAL_NANO..SITEMAP_MAX_ATTACK_REQUEST_INTERVAL_NANO)
        return dateTimeCurrentEntry.plusNanos(addNano)
    }

    private fun recipeBFFIncreaseCallDateTime(dateTimeCurrentEntry: LocalDateTime): LocalDateTime {
        val addNano = randomLongFromRange(RECIPEBFF_MIN_ATTACK_REQUEST_INTERVAL_NANO.toLong()..RECIPEBFF_MAX_ATTACK_REQUEST_INTERVAL_NANO.toLong())
        return dateTimeCurrentEntry.plusNanos(addNano)
    }

    private fun generateRandomIp(): String {
        return "${randomIntFromRange(1..254)}.${randomIntFromRange(0..255)}.${randomIntFromRange(0..255)}.${randomIntFromRange(0..255)}"
    }

    private fun randomRecipeBFFDate(): LocalDate {
        return recipeBFFPossibleDatesRequest[randomIntFromRange(0..<recipeBFFPossibleDatesRequest.size)]
    }

    private fun randomIntFromRange(intRange: IntRange): Int {
        return intRange.random()
    }

    private fun randomLongFromRange(longRange: LongRange): Long {
        return longRange.random()
    }
}

operator fun LocalDate.rangeTo(other: LocalDate) = DateProgression(this, other)

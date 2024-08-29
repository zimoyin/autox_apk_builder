package com.github.zimoyin.autox.builder.tools

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonPointer
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.zimoyin.autox.builder.tools.JsonObject.Companion.parseFields
import org.intellij.lang.annotations.Language
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.util.function.Consumer

object JsonUtils {
//    val objectMapper: ObjectMapper = ObjectMapper().registerModule(
//        KotlinModule()
//    )

    val objectMapper: ObjectMapper = ObjectMapper().apply {

        // Jackson框架的扩展点，允许用户通过模块化的方式添加对特定功能或数据类型的序列化和反序列化支持。
        // 模块（Module）设计的主要目的是为了保持Jackson核心库的轻量化，同时又能够灵活地扩展以适应多样化的数据处理需求。
        // 默认被配置的 Jackson Module 有 Jdk8Module, JavaTimeModule, JodaModule, KotlinModule
        registerModule(
            KotlinModule.Builder()
                .configure(KotlinFeature.NullToEmptyCollection, true) // 如果反序列化时属性为null，将他设置为空集合
                .configure(KotlinFeature.NullToEmptyMap, true) // 如果反序列化时属性为null，将他设置为空集合
                .configure(KotlinFeature.NullIsSameAsDefault, false) //在反序列化时不将值视为 null 不存在(转而使用 Kotlin 中提供的默认值)
                .configure(KotlinFeature.SingletonSupport, false) // 不对单例进行特殊处理
                .configure(KotlinFeature.StrictNullChecks, false) // 允许 null 成员的集合（例如 List<String>）在反序列化后可能包含 null 值。
                .build()
        )

        // 在反序列化时忽略在 JSON 中存在但 Java 对象不存在的属性
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        // 在缺少构造器参数时不抛出异常（默认）
        configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false)
        // 在构造器参数为 null 时不抛出异常（默认）
        configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false)

        // 在序列化时日期格式不为 yyyy-MM-dd'T'HH:mm:ss.SSSZ ， 而是时间戳（默认）
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)


        // 不允许在序列化数组时解包单个元素。当数组仅包含一个元素时，返回该元素而不是数组。
        configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, false)
        // 不允许在反序列化数组时解包单个元素。当数组仅包含一个元素时，返回该元素而不是数组。
        configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, false)

        // 以缩进的方式输出 JSON 数据，使其更易读。
        configure(SerializationFeature.INDENT_OUTPUT, true)

        // 在序列化时忽略值为自身引用的属性
        configure(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL, false)

        // 不忽略值为默认值的属性
        setDefaultPropertyInclusion(JsonInclude.Include.USE_DEFAULTS)
    }

    /**
     * 将对象序列化为Json字符串
     * 也可以用于字符串中出现了换行等需要转义的字符，该方法会自动转义
     */
    fun writeValueAsString(value: Any?): String {
        return objectMapper.writeValueAsString(value)
    }

    /**
     * Create a new JsonNode
     */
    fun newJsonObject(): ObjectNode {
        return objectMapper.createObjectNode()
    }

    /**
     * Create a new JsonNode
     */
    fun newJsonArray(): ArrayNode {
        return objectMapper.createArrayNode()
    }

    /**
     * JsonString to Map
     */
    inline fun <reified K, reified V> stringToMap(jsonString: String): Map<K, V> {
        val typeFactory = objectMapper.typeFactory
        val mapType = typeFactory.constructMapType(Map::class.java, K::class.java, V::class.java)
        return objectMapper.readValue(jsonString, mapType)
    }

    /**
     * Json String to JsonNode
     */
    fun stringToJsonNode(jsonString: String): JsonNode {
        return objectMapper.readTree(jsonString)
    }

    /**
     * JsonString to Bean
     */
    inline fun <reified T> stringToObject(jsonString: String): T {
        return objectMapper.readValue(jsonString)
    }

    /**
     * JsonString to List
     */
    inline fun <reified T> stringToList(jsonString: String): List<T> {
        val typeFactory = objectMapper.typeFactory
        val listType = typeFactory.constructCollectionType(List::class.java, T::class.java)
        return objectMapper.readValue(jsonString, listType)
    }


    /**
     * Bean to JsonNode
     *
     * 注意：如果对象是 String 的话不会解析 json 的
     */
    fun objectToJsonNode(obj: Any): JsonNode {
        return objectMapper.valueToTree(obj)
    }


    /**
     * Bean to JsonString
     */
    fun objectToJsonString(obj: Any): String {
        return objectMapper.writeValueAsString(obj)
    }

    /**
     * JsonNode to Bean
     */
    inline fun <reified T> jsonNodeToObject(json: JsonNode): T {
        return objectMapper.treeToValue(json, T::class.java)
    }


    /**
     * List to JsonNode
     */
    fun <T> listToJson(list: List<T>): JsonNode {
        return objectMapper.valueToTree(list)
    }

    /**
     * JsonNode to List
     */
    inline fun <reified T> jsonToList(json: JsonNode): List<T> {
        val typeFactory = objectMapper.typeFactory
        val listType = typeFactory.constructCollectionType(List::class.java, T::class.java)
        return objectMapper.convertValue(json, listType)
    }

    /**
     * List to JsonString
     */
    fun <T> listToJsonString(list: List<T>): String {
        return objectMapper.writeValueAsString(list)
    }

    /**
     * Map to JsonNode
     */
    fun mapToJson(map: Map<*, *>): JsonNode {
        return objectMapper.valueToTree(map)
    }

    /**
     * Map to JsonString
     */
    inline fun <reified K, reified V> jsonToMap(json: JsonNode): Map<K, V> {
        val typeFactory = objectMapper.typeFactory
        val mapType = typeFactory.constructMapType(Map::class.java, K::class.java, V::class.java)
        return objectMapper.convertValue(json, mapType)
    }

    /**
     * Map to JsonString
     */
    fun mapToString(map: Map<*, *>): String {
        return objectMapper.writeValueAsString(map)
    }

    /**
     * 通过 JSON 指针(Path路径) 修改Json 中某个位置的值
     * 注意该方法只能用于修改。
     * 如果要用于新增，请确保新增的节点的符节点存在。如果是新增一个数组的元素，请确保数组的父节点存在并且该元素的索引等于该数组长度。
     */
    fun modifyJsonByPointer(jsonString: String, pointer: String, newValue: Any): JsonNode {
        val objectMapper = ObjectMapper()
        val root: JsonNode = objectMapper.readTree(jsonString)

        // 找到指针对应的父节点
        val parentPath = pointer.substringBeforeLast("/")
        val fieldName = pointer.substringAfterLast("/")

        // 如果父节点是ObjectNode，我们可以修改它的子节点
        when (val parentNode = root.at(parentPath)) {
            is ObjectNode -> {
                // 根据字段类型处理新值
                when (newValue) {
                    is Char -> parentNode.put(fieldName, newValue.toString())
                    is Short -> parentNode.put(fieldName, newValue)
                    is String -> parentNode.put(fieldName, newValue)
                    is Int -> parentNode.put(fieldName, newValue)
                    is Boolean -> parentNode.put(fieldName, newValue)
                    is Double -> parentNode.put(fieldName, newValue)
                    is JsonNode -> parentNode.set<JsonNode>(fieldName, newValue)
                    is Byte -> parentNode.put(fieldName, newValue.toInt())
                    is Long -> parentNode.put(fieldName, newValue)
                    is Float -> parentNode.put(fieldName, newValue)
                    is BigInteger -> parentNode.put(fieldName, newValue)
                    is BigDecimal -> parentNode.put(fieldName, newValue)
                    is Number -> parentNode.put(fieldName, newValue.toDouble())
                    else -> parentNode.set(fieldName, newValue.toJsonNode())
                }
            }

            is ArrayNode -> {
                val index = fieldName.toInt()
                when (newValue) {
                    is Char -> parentNode.set(index, newValue.toString())
                    is Short -> parentNode.set(index, newValue)
                    is String -> parentNode.set(index, newValue)
                    is Int -> parentNode.set(index, newValue)
                    is Boolean -> parentNode.set(index, newValue)
                    is Double -> parentNode.set(index, newValue)
                    is JsonNode -> parentNode.set(index, newValue)
                    is Byte -> parentNode.set(index, newValue.toInt())
                    is Long -> parentNode.set(index, newValue)
                    is Float -> parentNode.set(index, newValue)
                    is BigInteger -> parentNode.set(index, newValue)
                    is BigDecimal -> parentNode.set(index, newValue)
                    is Number -> parentNode.set(index, newValue.toDouble())
                    else -> parentNode.set(index, newValue.toJsonNode())
                }
            }

            else -> {
                throw IllegalArgumentException("Pointer does not refer to a valid field")
            }
        }

        return root
    }

    fun modifyJsonByPointer(jsonString: JsonNode, pointer: String, newValue: Any): JsonNode {
        return modifyJsonByPointer(jsonString.toString(), pointer, newValue)
    }
}


/**
 * JsonObject 是 ObjectNode 的封装
 */
class JsonObject(
    val objectMapper: ObjectMapper = JsonUtils.objectMapper,
    private val kids: Map<String, JsonNode> = linkedMapOf(),
    val jsonParent: JsonObjectParent? = null,
    val arrayParent: JsonArray.JsonArrayParent? = null,
) : ObjectNode(objectMapper.nodeFactory, kids) {

    init {
        objectMapper.setNodeFactory(objectMapper.nodeFactory)
    }

    /**
     * 获取JsonPointer
     * @param key 键, 如果为null则获取当前节点的Path，不精确到子项
     */
    fun getJsonPointer(key: String? = null): JsonPointer {
        require(!(jsonParent != null && arrayParent != null)) { "There are two parent nodes present" }
        var jp = jsonParent
        var ap = arrayParent
        val path = StringBuilder()
        while (true) {
            if (jp != null) {
                path.insert(0, jp.name).insert(0, "/")
                val parent = jp.parent
                jp = parent.jsonParent
                ap = parent.arrayParent
                continue
            }
            if (ap != null) {
                path.insert(0, ap.index).insert(0, "/")
                val parent = ap.parent
                jp = parent.jsonParent
                ap = parent.arrayParent
                continue
            }
            break
        }

        return if (path.isEmpty()) {
            JsonPointer.compile("/${key ?: ""}")
        } else {
            JsonPointer.compile("$path${if (key == null) "" else "/$key"}")
        }
    }

    override fun _put(fieldName: String?, value: JsonNode?): ObjectNode {
        val node = super._put(fieldName, value)
        // 更新对象树
        jsonParent?.apply {
            this.parent._put(this.name, node)
        }
        arrayParent?.apply {
            this.parent.set(this.index, node)
        }
        return node
    }

    override fun replace(propertyName: String?, value: JsonNode?): JsonNode {
        _put(propertyName, value)
        return try {
            super.replace(propertyName, value)
        } catch (e: Exception) {
            throw IllegalArgumentException("replace error. key: $propertyName  value: $value", e)
        }
    }

    override fun <T : JsonNode?> set(propertyName: String?, value: JsonNode?): T {
        _put(propertyName, value)
        return super.set(propertyName, value)
    }

    override fun <T : JsonNode?> setAll(other: ObjectNode?): T {
        other?.fields()?.forEach {
            _put(it.key, it.value)
        }
        return super.setAll(other)
    }

    override fun <T : JsonNode?> setAll(properties: MutableMap<String, out JsonNode>?): T {
        properties?.forEach {
            _put(it.key, it.value)
        }
        return super.setAll(properties)
    }

    constructor(
        @Language("JSON") json: String,
        objectMapper: ObjectMapper = JsonUtils.objectMapper,
    ) : this(
        kids = parseFields(json, objectMapper),
        objectMapper = objectMapper
    )

    fun put(fieldName: String?, v: Any?): ObjectNode {
        return this.apply {
            val jsonNode: JsonNode = objectMapper.valueToTree(v)
            this.replace(fieldName, jsonNode)
        }
    }

    operator fun set(fieldName: String, v: Any?): ObjectNode {
        return put(fieldName, v)
    }


    fun put(fieldName: String?, v: JsonArray?): JsonObject {
        return this.apply {
            this.replace(fieldName, v?.toJsonNode()) // 使用replace方法插入或更新字段
        }
    }

    fun put(fieldName: String?, v: JsonObject?): JsonObject {
        return this.apply {
            this.replace(fieldName, v?.toJsonNode()) // 使用replace方法插入或更新字段
        }
    }

    fun putNull(fieldName: Any): JsonObject {
        return this.apply {
            this.replace(fieldName.toJsonNode().asText(), null)
        }
    }

    @Deprecated("please use getByPointer()")
    override fun path(index: Int): JsonNode {
        return super.path(index)
    }

    @Deprecated("please use getByPointer()")
    override fun path(propertyName: String?): JsonNode {
        return super.path(propertyName)
    }

    @Deprecated("please use getByPointer(pointer: JsonPointer)")
    fun path(pointer: JsonPointer): JsonNode? {
        return this.at(pointer)
    }

    /**
     * 通过JSON指针获取值
     */
    fun getByPointer(pointer: String): JsonNode? {
        return this.at(pointer)
    }

    fun getByPointer(pointer: JsonPointer): JsonNode? {
        return this.at(pointer)
    }

    /**
     * 通过JSON指针修改值
     */
    fun setByPointer(pointer: String, value: JsonNode): JsonNode {
        return JsonUtils.modifyJsonByPointer(this, pointer, value)
    }

    constructor(json: JsonNode, objectMapper: ObjectMapper = JsonUtils.objectMapper) : this(
        kids = parseFields(json),
        objectMapper = objectMapper
    )


    inline fun <reified T> toJavaObject(clazz: Class<T> = T::class.java): T {
        return objectMapper.treeToValue(this, clazz)
    }

    inline fun <reified T> parseToObject(): T {
        return objectMapper.treeToValue(this, T::class.java)
    }

    fun toJsonNode(): JsonNode {
        return this
    }

    fun toObjectNode(): ObjectNode {
        return this
    }

    fun toJsonString(): String {
        return JsonUtils.objectToJsonString(this.toJsonNode())
    }


    @Deprecated(
        "Please use get(key: String)",
        ReplaceWith("get(String)", "io.github.zimoyin.tools.web.util.json.JsonObject")
    )
    override fun get(index: Int): JsonNode {
        return super.get(index)
    }

    fun getLong(name: String): Long {
        val value = this.get(name) ?: throw IllegalArgumentException("JsonObject $name value not found")
        require(value.isNumber) { "JsonObject $name value not is Number type " }
        return value.asLong()
    }

    fun getInt(name: String): Int {
        val value = this.get(name) ?: throw IllegalArgumentException("JsonObject $name value not found")
        require(value.isNumber) { "JsonObject $name value not is Number type " }
        return value.asInt()
    }

    fun getDouble(name: String): Double {
        val value = this.get(name) ?: throw IllegalArgumentException("JsonObject $name value not found")
        require(value.isNumber) { "JsonObject $name value not is Number type " }
        return value.asDouble()
    }

    fun getString(name: String): String {
        val value = this.get(name) ?: throw IllegalArgumentException("JsonObject $name value not found")
        require(value.isTextual) { "JsonObject $name value not is String type " }
        return value.asText()
    }

    fun getBoolean(name: String): Boolean {
        val value = this.get(name) ?: throw IllegalArgumentException("JsonObject $name value not found")
        require(value.isBoolean) { "JsonObject $name value not is Boolean type " }
        return value.asBoolean()
    }

    /**
     * 获取 JsonObject，该JsonObject不是Json树上的节点，而是将树上的节点进行了拷贝，之后对 JsonObject 维护的树进行更新
     */
    fun getJsonObject(name: String): JsonObject {
        val get = this.get(name)
        require(!(get == null || get.toString().isEmpty())) { "JsonObject $name value not found" }
        return JsonObject(
            objectMapper = objectMapper,
            kids = parseFields(get),
            jsonParent = JsonObjectParent(parent = this, name = name)
        )
    }

    /**
     * 获取 JsonArray，该JsonArray不是Json树上的节点，而是将树上的节点进行了拷贝，之后对 JsonArray 维护的树进行更新
     */
    fun getJsonArray(name: String): JsonArray {
        val get = this.get(name)
        require(!(get == null || get.toString().isEmpty())) { "JsonObject $name value not found" }
        return JsonArray(
            objectMapper = objectMapper,
            elements = JsonArray.parseElements(get),
            jsonParent = JsonObjectParent(parent = this, name = name)
        )
    }

    /**
     * 获取对象
     */
    inline fun <reified T> getObject(name: String): T {
        return objectMapper.treeToValue(get(name), T::class.java)
    }

    /**
     * 获取ObjectNode，该 ObjectNode是节点树上的节点，修改他整棵树都会更新
     */
    fun getObjectNode(s: String): ObjectNode {
        val get = get(s)
        require(!(get == null || get.toString().isEmpty() || !get.isObject)) { "ObjectNode $s value not found" }
        return get(s) as ObjectNode
    }

    /**
     * 获取ArrayNode，该 ArrayNode是节点树上的节点，修改他整棵树都会更新
     */
    fun getArrayNode(s: String): ArrayNode {
        val get = get(s)
        require(!(get == null || get.toString().isEmpty() || !get.isArray)) { "ArrayNode $s value not found" }
        return get(s) as ArrayNode
    }

    fun toMap(): Map<String, JsonNode> {
        return fields().asSequence().associate { it.key to it.value }
    }

    fun foreach(action: Consumer<in Map.Entry<String, JsonNode>>) {
        fields().forEach {
            action.accept(it)
        }
    }

    fun foreach(action: (Map.Entry<String, JsonNode>) -> Unit) {
        fields().forEach {
            action(it)
        }
    }


    val keys: MutableSet<String> = fieldNames().asSequence().toMutableSet()
    val value: MutableList<JsonNode> = fields().asSequence().map { it.value }.toMutableList()

    companion object {
        private val GlobalObjectMapper: ObjectMapper = JsonUtils.objectMapper

        fun parseObject(
            value: Any,
            objectMapper: ObjectMapper = GlobalObjectMapper,
        ): JsonObject {
            // 将JsonNode转换为JsonObject并返回
            return if (value is String) {
                try {
                    return JsonObject(objectMapper = objectMapper, kids = parseFields(value))
                } catch (e: IllegalArgumentException) {
                    return JsonObject(
                        objectMapper = objectMapper,
                        kids = parseFields(JsonUtils.objectToJsonNode(value))
                    )
                }
            } else {
                JsonObject(objectMapper = objectMapper, kids = parseFields(JsonUtils.objectToJsonNode(value)))
            }
        }

        fun parse(
            @Language("JSON") value: String,
            objectMapper: ObjectMapper = GlobalObjectMapper,
        ): JsonObject {
            // 将JsonNode转换为JsonObject并返回
            return JsonObject(objectMapper = objectMapper, kids = parseFields(value))
        }

        fun parseFields(
            @Language("JSON") value: String,
            objectMapper: ObjectMapper = GlobalObjectMapper,
        ): HashMap<String, JsonNode> {
            // 使用ObjectMapper将JSON字符串转换为JsonNode
            val jsonNode: JsonNode = objectMapper.readTree(value)

            // 检查是否为ObjectNode类型，如果不是，则可能需要抛出异常
            require(jsonNode is ObjectNode) { "Provided JSON string does not represent an object. json: $jsonNode" }

            // 从JsonNode构建一个新的JsonObject
            val map = HashMap<String, JsonNode>()
            jsonNode.fields().forEach { entry ->
                map[entry.key] = entry.value
            }
            return map
        }

        fun parseFields(jsonNode: JsonNode): HashMap<String, JsonNode> {
            require(jsonNode is ObjectNode) { "Provided JSON string does not represent an object. json: $jsonNode" }
            val map = HashMap<String, JsonNode>()
            jsonNode.fields().forEach { entry ->
                map[entry.key] = entry.value
            }
            return map
        }
    }

    class JsonObjectParent(
        val parent: JsonObject,
        val name: String,
    )
}

fun jsonObjectOf(vararg objs: Pair<Any, Any?>): JsonObject {
    val jsonObject = JsonObject()
    objs.forEach {
        jsonObject.put(it.first.toString(), it.second)
    }
    return jsonObject
}

fun JsonNode.asJsonObject(): JsonObject = JsonObject(objectMapper = JsonUtils.objectMapper, kids = parseFields(this))

/**
 * JsonArray 是 ArrayNode 的封装
 */
class JsonArray(
    private val elements: List<Any> = listOf(),
    val objectMapper: ObjectMapper = JsonUtils.objectMapper,
    val jsonParent: JsonObject.JsonObjectParent? = null,
    val arrayParent: JsonArrayParent? = null,
) : ArrayNode(objectMapper.nodeFactory, elements.map {
    if (it is JsonNode) it
    else objectMapper.valueToTree<JsonNode>(it)
}) {

    constructor(
        @Language("JSON") json: String,
        objectMapper: ObjectMapper = JsonUtils.objectMapper,
    ) : this(
        elements = parseElements(json, objectMapper),
        objectMapper = objectMapper
    )

    constructor(
        vararg array: Any,
        objectMapper: ObjectMapper = JsonUtils.objectMapper,
    ) : this(
        elements = array.map { objectMapper.valueToTree<JsonNode>(it) },
        objectMapper = objectMapper
    )

    /**
     * 获取 JsonPointer
     * @param key 索引，默认为空的话则是获取当前对象的Path
     */
    fun getJsonPointer(key: Int? = null): JsonPointer {
        require(!(jsonParent != null && arrayParent != null)) { "There are two parent nodes present" }
        var jp = jsonParent
        var ap = arrayParent
        val path = StringBuilder()
        while (true) {
            if (jp != null) {
                path.insert(0, jp.name).insert(0, "/")
                val parent = jp.parent
                jp = parent.jsonParent
                ap = parent.arrayParent
                continue
            }
            if (ap != null) {
                path.insert(0, ap.index).insert(0, "/")
                val parent = ap.parent
                jp = parent.jsonParent
                ap = parent.arrayParent
                continue
            }
            break
        }

        return if (path.isEmpty()) {
            JsonPointer.compile("/${key ?: ""}")
        } else {
            JsonPointer.compile("$path${if (key == null) "" else "/$key"}")
        }
    }

    fun forEachJsonObject(content: (JsonObject) -> Unit) {
        for (i in 0 until this.size()) {
            content(getJsonObject(i))
        }
    }

    override fun _add(node: JsonNode?): ArrayNode {
        val add = super._add(node)
        // 更新对象树
        jsonParent?.apply {
            this.parent.replace(this.name, add)
        }
        arrayParent?.apply {
            this.parent.set(this.index, node)
        }
        return add
    }

    override fun _set(index: Int, node: JsonNode?): ArrayNode {
        val set = super._set(index, node)
        // 更新对象树
        jsonParent?.apply {
            this.parent.replace(this.name, set)
        }
        arrayParent?.apply {
            this.parent.set(this.index, node)
        }
        return set
    }

    override fun set(index: Int, value: JsonNode?): JsonNode {
        val set = super.set(index, value)
        val current = this
        // 更新对象树
        jsonParent?.apply {
            this.parent.replace(this.name, current)
        }
        arrayParent?.apply {
            this.parent.set(this.index, value)
        }
        return set
    }

    fun add(v: JsonObject?): ArrayNode {
        return add(v?.toJsonNode())
    }

    fun add(v: JsonArray?): ArrayNode {
        return add(v?.toJsonNode())
    }

    fun add(v: Any?): ArrayNode? {
        return add(v?.toJsonNode())
    }

    fun set(index: Int, v: JsonObject?): JsonNode? {
        return this.set(index, v?.toJsonNode())
    }

    fun set(index: Int, v: JsonArray?): JsonNode? {
        return this.set(index, v?.toJsonNode())
    }

    fun set(index: Int, v: Any?): JsonNode? {
        return this.set(index, v?.toJsonNode())
    }


    /**
     * 通过JSON指针修改值
     */
    fun setByPointer(pointer: String, value: JsonNode): JsonNode {
        return JsonUtils.modifyJsonByPointer(this, pointer, value)
    }

    fun toJsonNode(): JsonNode {
        return this
    }

    fun toArrayNode(): ArrayNode {
        return this
    }

    inline fun <reified T> parseToObject(): T {
        return objectMapper.treeToValue(this, T::class.java)
    }

    override fun get(index: Int): JsonNode? {
        if (index < 0 || index > size() - 1) throw IndexOutOfBoundsException("Index out of range [0,${size() - 1}]: $index")
        return super.get(index)
    }

    /**
     * 通过JSON指针获取值
     */
    fun getByPointer(pointer: String): JsonNode? {
        return this.at(pointer)
    }

    fun getByPointer(pointer: JsonPointer): JsonNode? {
        return this.at(pointer)
    }

    fun getLong(index: Int): Long {
        return this.get(index)?.asLong() ?: throw IllegalArgumentException("JsonArray $index value not found")
    }

    fun getInt(index: Int): Int {
        return this.get(index)?.asInt() ?: throw IllegalArgumentException("JsonArray $index value not found")
    }

    fun getDouble(index: Int): Double {
        return this.get(index)?.asDouble() ?: throw IllegalArgumentException("JsonArray $index value not found")
    }

    fun getString(index: Int): String {
        return this.get(index)?.asText() ?: throw IllegalArgumentException("JsonArray $index value not found")
    }

    fun getBoolean(index: Int): Boolean {
        return this.get(index)?.asBoolean() ?: throw IllegalArgumentException("JsonArray $index value not found")
    }

    /**
     * 获取JsonArray，该JsonArray不是Json树上的节点，而是将树上的节点进行了拷贝，之后对 JsonArray 维护的树进行更新
     */
    fun getJsonArray(index: Int): JsonArray {
        val get = this.get(index)
        require(!(get == null || get.toString().isEmpty())) { "JsonArray $index value not found;" }
        return JsonArray(
            objectMapper = objectMapper,
            elements = parseElements(get),
            arrayParent = JsonArrayParent(this, index)
        )
    }

    /**
     * 获取 JsonObject，该JsonObject不是Json树上的节点，而是将树上的节点进行了拷贝，之后对 JsonObject 维护的树进行更新
     */
    fun getJsonObject(index: Int): JsonObject {
        val get = this.get(index)
        require(!(get == null || get.toString().isEmpty())) { "JsonArray $index value not found; json: $this" }
        return JsonObject(
            objectMapper = objectMapper,
            kids = JsonObject.parseFields(get),
            arrayParent = JsonArrayParent(this, index)
        )
    }

    /**
     * 获取ObjectNode，该 ObjectNode是节点树上的节点，修改他整棵树都会更新
     */
    fun getObjectNode(index: Int): ObjectNode {
        val get = get(index)
        require(!(get == null || get.toString().isEmpty() || !get.isObject)) { "ObjectNode $get value not found" }
        return get as ObjectNode
    }

    /**
     * 获取ArrayNode，该 ArrayNode是节点树上的节点，修改他整棵树都会更新
     */
    fun getArrayNode(index: Int): ArrayNode {
        val get = get(index)
        require(!(get == null || get.toString().isEmpty() || !get.isArray)) { "ArrayNode $get value not found" }
        return get as ArrayNode
    }

    inline fun <reified T> getObject(index: Int): T {
        return objectMapper.treeToValue(get(index), T::class.java)
    }

    companion object {
        private val GlobalObjectMapper: ObjectMapper = JsonUtils.objectMapper

        fun parseArray(
            @Language("JSON") value: String,
            objectMapper: ObjectMapper = GlobalObjectMapper,
        ): JsonArray {
            // Convert JSON array string to List<JsonNode> and return JsonArray
            return JsonArray(objectMapper = objectMapper, elements = parseElements(value, objectMapper))
        }

        fun parseElements(
            @Language("JSON") value: String,
            objectMapper: ObjectMapper = GlobalObjectMapper,
        ): List<JsonNode> {
            // Use ObjectMapper to convert JSON array string to List<JsonNode>
            val jsonNode: JsonNode = objectMapper.readTree(value)

            // Check if it's an ArrayNode, if not, throw an exception
            require(jsonNode is ArrayNode) { "Provided JSON string does not represent an array." }

            // Return the elements of the array
            return jsonNode.elements().asSequence().toList()
        }

        fun parseArray(jsonNode: JsonNode): JsonArray {
            return JsonArray(objectMapper = GlobalObjectMapper, elements = parseElements(jsonNode))
        }

        fun parseElements(jsonNode: JsonNode): List<JsonNode> {
            require(jsonNode is ArrayNode) { "Provided JSON string does not represent an array." }
            return jsonNode.elements().asSequence().toList()
        }

        fun parseArray(
            list: List<Any>,
            objectMapper: ObjectMapper = GlobalObjectMapper,
        ): JsonArray {
            return JsonArray(
                elements = list.map {
                    if (it is JsonNode) it
                    else objectMapper.valueToTree<JsonNode>(it)
                },
                objectMapper = objectMapper
            )
        }

        fun parseArray(array: Array<Any>, objectMapper: ObjectMapper = GlobalObjectMapper): JsonArray {
            return JsonArray(
                elements = array.map {
                    if (it is JsonNode) it
                    else objectMapper.valueToTree<JsonNode>(it)
                },
                objectMapper = objectMapper
            )
        }
    }

    class JsonArrayParent(
        val parent: JsonArray,
        val index: Int,
    )
}

fun JsonNode.asJsonArray(): JsonArray = JsonArray.parseArray(this)

fun JsonNode.serializationString(): String = JsonUtils.writeValueAsString(this)

/**
 * JsonObject DSL
 *
 *
 *     val json = jsonObject {
 *         "name" to "John"
 *         put("age", 30)
 *         "address" to jsonObject {
 *             "street" to "Main St."
 *             "city" to "New York"
 *             put("zip") // null
 *         }
 *         jsonObject("city"){
 *             "street" to "Main St."
 *             // ... jsonObject
 *         }
 *         "phone" to jsonArray {
 *             add("123-456-7890")
 *             add("123-456-7890")
 *             add("123-456-7890")
 *             set(1, "987-654-3210")
 *             "123-456-7890" insert 2
 *         }
 *         "email" to array("john@example.com", "john.doe@example.com")
 *     }
 */
@DslMarker
annotation class JsonDSL

/**
 * JsonObject DSL
 */
fun jsonObject(content: JsonObjectSpace.() -> Unit): JsonObject {
    return JsonObjectSpace().apply(content).json
}

/**
 * JsonArray DSL
 */
fun jsonArray(content: JsonArraySpace.() -> Unit): JsonArray {
    return JsonArraySpace().apply(content).json
}


@JsonDSL
class JsonObjectSpace(
    val json: JsonObject = JsonObject(),
) {
    infix fun Any.to(value: Any?) {
        json.put(this.toString(), value)
    }

    fun put(key: Any, value: Any?) {
        json.put(key.toString(), value)
    }

    fun put(key: Any) {
        json.putNull(key)
    }

    fun jsonObject(key: Any, content: JsonObjectSpace.() -> Unit) {
        val json1 = JsonObjectSpace().apply(content).json
        json.put(key.toString(), json1)
    }

    fun jsonArray(content: JsonArraySpace.() -> Unit): JsonArray {
        return JsonArraySpace().apply(content).json
    }

    fun array(vararg array: Any): JsonArray {
        return JsonArray(*array)
    }
}

@JsonDSL
class JsonArraySpace(
    val json: JsonArray = JsonArray(),
) {

    fun json(value: JsonObjectSpace.() -> Unit) {
        json.add(JsonObjectSpace().apply(value).json)
    }

    fun array(value: JsonArraySpace.() -> Unit) {
        json.add(JsonArraySpace().apply(value).json)
    }

    fun add(value: Any?) {
        json.add(value)
    }

    fun set(index: Int, value: Any?) {
        json.set(index, value)
    }

    infix fun Any.insert(index: Int) {
        json.set(index, this)
    }
}


fun JsonNode.writeToFile(path: String) {
    File(path).writeText(this.toString())
}

/**
 *
 * @author : zimo
 * @date : 2024/02/28
 */
fun Any.toJsonNode(): JsonNode {
    return if (this is String) {
        try {
            return JsonUtils.stringToJsonNode(this)
        } catch (e: JsonParseException) {
            return JsonUtils.objectToJsonNode(this)
        }
    } else {
        JsonUtils.objectToJsonNode(this)
    }
}

fun Any.toJsonObject(): JsonObject {
    return if (this is String) {
        JsonObject.parseObject(this)
    } else {
        JsonObject.parseObject(this).apply {
            if (this.isEmpty) throw Exception("The object does not have a public decorated attribute")
        }
    }
}

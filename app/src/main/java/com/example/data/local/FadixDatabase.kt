package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CartItemEntity
import com.example.data.model.Categories
import com.example.data.model.CustomerProfileEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItemEntity
import com.example.data.model.ProductEntity
import kotlinx.coroutines.CoroutineScope
import androidx.room.migration.Migration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE products ADD COLUMN unitsPerPackage INTEGER NOT NULL DEFAULT 12")
    }
}

@Database(
    entities = [
        ProductEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        CustomerProfileEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class FadixDatabase : RoomDatabase() {
    abstract fun fadixDao(): FadixDao

    companion object {
        @Volatile
        private var INSTANCE: FadixDatabase? = null

        fun getDatabase(context: Context): FadixDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FadixDatabase::class.java,
                    "fadix_wholesale_database.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.fadixDao()?.let { dao ->
                                    seedInitialProducts(dao)
                                    updateMissingImages(dao)
                                }
                            }
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.fadixDao()?.let { dao ->
                                    seedInitialProducts(dao)
                                    updateMissingImages(dao)
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun updateMissingImages(dao: FadixDao) {
            val products = dao.getAllProductsList()
            for (p in products) {
                val realUrl = com.example.ui.components.getProductImageUrl(p.name, p.category)
                if (p.imageUrl != realUrl) {
                    dao.updateProduct(p.copy(imageUrl = realUrl))
                }
            }
        }

        suspend fun seedInitialProducts(dao: FadixDao) {
            if (dao.getProductCount() > 0) return

            val initialCatalog = listOf(
                // 1. المواد الأكثر طلباً ومبيعاً (Exact prompt examples)
                ProductEntity(
                    name = "شاي أخضر طبيعي",
                    category = Categories.FOOD,
                    wholesalePrice = 450.00,
                    packageUnit = "كرتونة",
                    unitsPerPackage = 24,
                    stock = 50,
                    description = "شاي أخضر طبيعي مغربي أصيل ذو نكهة رائعة وإقبال استهلاكي واسع."
                ),
                ProductEntity(
                    name = "زيت المائدة (1 لتر)",
                    category = Categories.FOOD,
                    wholesalePrice = 320.00,
                    packageUnit = "كرتونة",
                    unitsPerPackage = 12,
                    stock = 60,
                    description = "زيت نباتي مكرر ممتاز للطبخ والقلي عالي الجودة للبقالات."
                ),
                ProductEntity(
                    name = "سطار - مايونيز 37 سنتيلتر",
                    category = Categories.FOOD,
                    wholesalePrice = 170.13,
                    packageUnit = "كرتونة",
                    unitsPerPackage = 12,
                    stock = 45,
                    description = "مايونيز سطار عالي الجودة 37 سنتيلتر كرتونة 12 وحدة للمتاجر والبقالات."
                ),
                // مشروبات غازية وطاقة (Matches Reference Image)
                ProductEntity(
                    name = "ستينغ بوري بلاست بلاستيك 25 سل",
                    category = Categories.DRINKS,
                    wholesalePrice = 81.00,
                    packageUnit = "باكية",
                    unitsPerPackage = 18,
                    stock = 50,
                    description = "مشروب الطاقة ستينغ بيري بلاست نكهة الفراولة المنعشة 25 سل عبوة بلاستيكية."
                ),
                ProductEntity(
                    name = "مشروب الطاقة ستينغ صفرة 25 سل",
                    category = Categories.DRINKS,
                    wholesalePrice = 81.00,
                    packageUnit = "باكية",
                    unitsPerPackage = 18,
                    stock = 50,
                    description = "مشروب الطاقة ستينغ الأصفر الكلاسيكي 25 سل عبوة بلاستيكية."
                ),
                ProductEntity(
                    name = "ريد بول 25 سل",
                    category = Categories.DRINKS,
                    wholesalePrice = 19.00,
                    packageUnit = "كانز",
                    unitsPerPackage = 1,
                    stock = 80,
                    description = "مشروب الطاقة العالمي ريد بول كانز 25 سل."
                ),
                ProductEntity(
                    name = "سميد الصلب 1 كغ",
                    category = Categories.FOOD,
                    wholesalePrice = 85.00,
                    packageUnit = "كرتون",
                    unitsPerPackage = 10,
                    stock = 60,
                    description = "سميد قمح صلب فاخر وممتاز لتحضير الحرشة والخبز والحلويات."
                ),
                ProductEntity(
                    name = "أرز بسمتي 5 كغ",
                    category = Categories.FOOD,
                    wholesalePrice = 480.00,
                    packageUnit = "كرتون",
                    unitsPerPackage = 4,
                    stock = 35,
                    description = "أرز بسمتي هندي حبة طويلة ممتاز للمطابخ والعائلات."
                ),
                ProductEntity(
                    name = "عدس 1 كغ",
                    category = Categories.FOOD,
                    wholesalePrice = 120.00,
                    packageUnit = "كرتون",
                    unitsPerPackage = 10,
                    stock = 45,
                    description = "عدس حبوب طبيعية منتقاة بعناية لضمان أعلى جودة."
                ),
                ProductEntity(
                    name = "معلب طماطم",
                    category = Categories.FOOD,
                    wholesalePrice = 144.00,
                    packageUnit = "كرتون",
                    unitsPerPackage = 24,
                    stock = 70,
                    description = "طماطم معلبة مركزة ذات نكهة غنية للمرق والصلصات."
                ),
                ProductEntity(
                    name = "سكر كلاصي 1 كغ",
                    category = Categories.FOOD,
                    wholesalePrice = 160.00,
                    packageUnit = "كرتون",
                    unitsPerPackage = 10,
                    stock = 55,
                    description = "سكر ناعم مطحون ممتاز لصنع الحلويات والتزيين."
                ),
                ProductEntity(
                    name = "صلصة طماطم عائشة مركزة 370 غ",
                    category = Categories.FOOD,
                    wholesalePrice = 78.0,
                    packageUnit = "كرتون (12 علبة زجاجية)",
                    stock = 60,
                    description = "طماطم مغربية مركزة عالية الجودة للمطابخ والمطاعم والمنازل."
                ),
                ProductEntity(
                    name = "دقيق ميمونة للحلويات والخبز الممتاز 10 كغ",
                    category = Categories.FOOD,
                    wholesalePrice = 52.0,
                    packageUnit = "كيس 10 كلغ محكم",
                    stock = 80,
                    description = "دقيق أبيض فاخر رقم 1 للمخابز ومحلات الحلويات والطهي المنزلي."
                ),
                ProductEntity(
                    name = "جبن كيري الأصلي 24 قطعة",
                    category = Categories.FOOD,
                    wholesalePrice = 138.0,
                    packageUnit = "كرتون (6 علب × 24 قطعة)",
                    stock = 40,
                    description = "مربعات الجبن الطازج القابل للدهن، طلب مستمر وسريع الحركة."
                ),
                ProductEntity(
                    name = "سكر سنيدة الأسرة كيس 2 كلغ",
                    category = Categories.FOOD,
                    wholesalePrice = 88.0,
                    packageUnit = "كرتون (8 أكياس × 2 كغ)",
                    stock = 55,
                    description = "سكر أبيض نقي بلوري فاخر للطبخ والحلويات والمشروبات."
                ),
                ProductEntity(
                    name = "كسكس داري متوسط 1 كلغ",
                    category = Categories.FOOD,
                    wholesalePrice = 115.0,
                    packageUnit = "كرتون (10 علب × 1 كغ)",
                    stock = 35,
                    description = "كسكس مغربي أصيل بحبات متوسطة مطبوخ بالبخار."
                ),

                // 2. مشروبات وعصائر (Drinks)
                ProductEntity(
                    name = "كوكاكولا أصلي 1 لتر زجاج/بلاستيك",
                    category = Categories.DRINKS,
                    wholesalePrice = 84.0,
                    packageUnit = "صندوق (12 قارورة × 1 لتر)",
                    stock = 50,
                    description = "المشروب الغازي الأشهر عالمياً ومحلياً، بارد ومنعش."
                ),
                ProductEntity(
                    name = "مياه معدنية طبيعية سيدي علي 1.5 لتر",
                    category = Categories.DRINKS,
                    wholesalePrice = 28.0,
                    packageUnit = "حزمة (6 قنينات × 1.5 لتر)",
                    stock = 120,
                    description = "مياه شرب معدنية نقية وصحية من سيدي علي تيفليت."
                ),
                ProductEntity(
                    name = "عصير جودة برتقال طبيعي 1 لتر",
                    category = Categories.DRINKS,
                    wholesalePrice = 96.0,
                    packageUnit = "كرتون (12 علبة تتراباك)",
                    stock = 40,
                    description = "عصير فواكه غني بفيتامين ج وخالٍ من المواد الحافظة الضارة."
                ),
                ProductEntity(
                    name = "حليب سليم معقم كامل الدسم 1 لتر",
                    category = Categories.DRINKS,
                    wholesalePrice = 108.0,
                    packageUnit = "كرتون (12 علبة × 1 لتر)",
                    stock = 70,
                    description = "حليب مغربي مبستر UHT طويل الأجل وصحي للأطفال والكبار."
                ),
                ProductEntity(
                    name = "مشروب بومس تفاح غازي 33 سنتلتر",
                    category = Categories.DRINKS,
                    wholesalePrice = 120.0,
                    packageUnit = "كرتون كانز (24 كانز)",
                    stock = 45,
                    description = "نكهة التفاح المنعشة المفضلة لدى عملاء البقالات."
                ),

                // 3. بسكويت وكوكيز (Biscuits)
                ProductEntity(
                    name = "بسكويت بيمو غولدن الأصلي 50 غ",
                    category = Categories.BISCUITS,
                    wholesalePrice = 72.0,
                    packageUnit = "صندوق تجاري (36 باكت)",
                    stock = 65,
                    description = "بسكويت الشاي المقرمش الشهير، المفضل في الإفطار واللمجة."
                ),
                ProductEntity(
                    name = "بسكويت طوكيو محشو بكريمة الكاكاو",
                    category = Categories.BISCUITS,
                    wholesalePrice = 48.0,
                    packageUnit = "علبة عرض (24 باكت)",
                    stock = 50,
                    description = "ساندويتش بسكويت شوكولاتة سريع البيع للأطفال والشباب."
                ),
                ProductEntity(
                    name = "كوفريت توبينغ فانيلا كريسبي",
                    category = Categories.BISCUITS,
                    wholesalePrice = 60.0,
                    packageUnit = "علبة كرتونية (30 قطعة)",
                    stock = 40,
                    description = "ويفر مقرمش محشو بكريمة الفانيلا البلجيكية الفاخرة."
                ),
                ProductEntity(
                    name = "بسكويت صابلي الزبدة إيديال 75 غ",
                    category = Categories.BISCUITS,
                    wholesalePrice = 65.0,
                    packageUnit = "كرتون (12 علبة عائلية)",
                    stock = 35,
                    description = "صابلي هش محضر بالزبدة الطبيعية لنكهة تقليدية رائعة."
                ),

                // 4. حلويات وسكاكر (Sweets)
                ProductEntity(
                    name = "سكاكر كلير كراميل بالحليب الناعم",
                    category = Categories.SWEETS,
                    wholesalePrice = 45.0,
                    packageUnit = "كيس وزني (1 كلغ اقتصادي)",
                    stock = 40,
                    description = "حلوى كراميل طرية غنية بنكهة التوفي والزبدة."
                ),
                ProductEntity(
                    name = "مصاصات فواكه مشكلة بوبس ستيك",
                    category = Categories.SWEETS,
                    wholesalePrice = 55.0,
                    packageUnit = "علبة استاند (100 قطعة)",
                    stock = 60,
                    description = "نكهات فواكه متنوعة: فراولة، برتقال، ليمون، كولا."
                ),
                ProductEntity(
                    name = "علكة كلوروتس بالنعناع المنعش",
                    category = Categories.SWEETS,
                    wholesalePrice = 68.0,
                    packageUnit = "صندوق كاشير (40 باكت)",
                    stock = 45,
                    description = "علكة انتعاش الفم بالكلوروفيل والنعناع الفوري."
                ),
                ProductEntity(
                    name = "حلوى ميشيكا فراولة جيلاتينية",
                    category = Categories.SWEETS,
                    wholesalePrice = 42.0,
                    packageUnit = "كيس تجاري (1 كلغ)",
                    stock = 30,
                    description = "حلوى طرية ومبهجة بألوان جذابة تحظى بإقبال عالٍ."
                ),

                // 5. شوكولاتة (Chocolate)
                ProductEntity(
                    name = "شوكولاتة مارو بالبندق المحمص 100 غ",
                    category = Categories.CHOCOLATE,
                    wholesalePrice = 110.0,
                    packageUnit = "صندوق عرض (24 لوح)",
                    stock = 50,
                    description = "شوكولاتة بالحليب مع حبات بندق كاملة مقرمشة."
                ),
                ProductEntity(
                    name = "كيك ميريندينا شوكولاتة الأصلي",
                    category = Categories.CHOCOLATE,
                    wholesalePrice = 68.0,
                    packageUnit = "علبة كرتونية (24 باكت)",
                    stock = 75,
                    description = "الكيك المحشو والمغطى بالشوكولاتة الكلاسيكي الأكثر شهرة."
                ),
                ProductEntity(
                    name = "شوكولاتة كندر بوينو أصابع مقرمشة",
                    category = Categories.CHOCOLATE,
                    wholesalePrice = 210.0,
                    packageUnit = "صندوق استاند (30 باكت × أصبعين)",
                    stock = 30,
                    description = "ويفر رقيق محشو بكريمة البندق ومغطى بشوكولاتة الحليب."
                ),
                ProductEntity(
                    name = "شوكولاتة نوتيلا دهن البندق 400 غ",
                    category = Categories.CHOCOLATE,
                    wholesalePrice = 150.0,
                    packageUnit = "كرتون (6 برطمانات زجاجية)",
                    stock = 25,
                    description = "كريمة البندق والكاكاو الأصلية للإفطار وصنع الفطائر."
                ),

                // 6. رقائق ومقرمشات (Chips and Snacks)
                ProductEntity(
                    name = "بطاطس ليز مقرمشة نكهة الجبنة الفرنسية 40 غ",
                    category = Categories.SNACKS,
                    wholesalePrice = 85.0,
                    packageUnit = "كرتون (30 كيس فردي)",
                    stock = 50,
                    description = "رقائق بطاطس طازجة ومقرمشة ومتبلة بأفضل التوابل."
                ),
                ProductEntity(
                    name = "بطاطس برينجلز الأصلية أسطوانة 165 غ",
                    category = Categories.SNACKS,
                    wholesalePrice = 192.0,
                    packageUnit = "صندوق كرتون (12 أسطوانة)",
                    stock = 20,
                    description = "رقائق بطاطس مقرمشة متساوية الحجم بنكهة الملح والبطاطس النقية."
                ),
                ProductEntity(
                    name = "فشار باص باص بنكهة الزبدة اللذيذة",
                    category = Categories.SNACKS,
                    wholesalePrice = 48.0,
                    packageUnit = "كرتون عرض (24 كيس)",
                    stock = 40,
                    description = "ذرة مقرمشة مفشفشة بدون دهون مهدرجة وخفيفة على المعدة."
                ),
                ProductEntity(
                    name = "مقرمشات تشيبس طرطور بابريكا 45 غ",
                    category = Categories.SNACKS,
                    wholesalePrice = 52.0,
                    packageUnit = "كرتون (24 كيس مقرمش)",
                    stock = 35,
                    description = "سناك الذرة المتبل بالبابريكا الحلوة الشهية."
                )
            )

            dao.insertProducts(initialCatalog)

            // Seed default customer profile for seamless first experience
            dao.insertOrUpdateProfile(
                CustomerProfileEntity(
                    id = 1,
                    phone = "0661234567",
                    grocerName = "رشيد المنصوري",
                    shopName = "بقالة الخير والبركة",
                    deliveryAddress = "شارع الحسن الثاني، رقم 42، درب غلف",
                    deliveryZone = "داخل الدار البيضاء",
                    notes = "التوصيل في الفترة الصباحية من 9 إلى 12",
                    isLoggedIn = true
                )
            )

            // Seed a sample past wholesale order to show real history immediately
            val sampleOrder = OrderEntity(
                orderNumber = "FDX-2026-1084",
                customerPhone = "0661234567",
                grocerName = "رشيد المنصوري",
                shopName = "بقالة الخير والبركة",
                deliveryAddress = "شارع الحسن الثاني، رقم 42، درب غلف، الدار البيضاء",
                deliveryZone = "داخل الدار البيضاء",
                deliveryFee = 0.0,
                subtotal = 643.0,
                totalAmount = 643.0,
                notes = "التسليم في المخزن الخلفي للمحل",
                status = "تم التسليم",
                createdAt = System.currentTimeMillis() - 86400000L * 2
            )
            val orderId = dao.insertOrder(sampleOrder)

            val sampleItems = listOf(
                OrderItemEntity(
                    orderId = orderId,
                    productId = 1,
                    productName = "مكرونة إتقان سباغيتي 500 غرام",
                    packageUnit = "كرتون (20 علبة × 500 غ)",
                    unitPrice = 145.0,
                    quantity = 2,
                    subtotal = 290.0
                ),
                OrderItemEntity(
                    orderId = orderId,
                    productId = 2,
                    productName = "زيت المائدة لوسيور 5 لتر",
                    packageUnit = "صندوق (4 قنينات × 5 لتر)",
                    unitPrice = 260.0,
                    quantity = 1,
                    subtotal = 260.0
                ),
                OrderItemEntity(
                    orderId = orderId,
                    productId = 10,
                    productName = "مياه معدنية طبيعية سيدي علي 1.5 لتر",
                    packageUnit = "حزمة (6 قنينات × 1.5 لتر)",
                    unitPrice = 28.0,
                    quantity = 3,
                    subtotal = 84.0
                )
            )
            dao.insertOrderItems(sampleItems)
        }
    }
}

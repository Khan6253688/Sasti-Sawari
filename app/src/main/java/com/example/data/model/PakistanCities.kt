package com.example.data.model

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt

data class LocationPoint(
  val name: String,
  val urduName: String = "",
  val category: String, // Transport, Hospital, University, Market, Landmark, Residential
  val lat: Double,
  val lng: Double,
  val approxDistanceKmFromStand: Double = 0.0
)

data class CityInfo(
  val id: String,
  val name: String,
  val urduName: String,
  val tagLine: String,
  val hallmarkTitle: String,
  val hallmarkUrdu: String,
  val hallmarkDescription: String,
  val hallmarkIconType: String, // "shrine", "mosque", "monument", "tower", "palace", "fort", "hills"
  val defaultStandName: String,
  val defaultStandAddress: String,
  val centerLat: Double,
  val centerLng: Double,
  val locations: List<LocationPoint>
)

object PakistanCitiesData {
  val cities: List<CityInfo> = listOf(
    // 1. MULTAN
    CityInfo(
      id = "multan",
      name = "Multan",
      urduName = "ملتان",
      tagLine = "City of Saints & Sufis",
      hallmarkTitle = "Shah Rukn-e-Alam & Ghanta Ghar",
      hallmarkUrdu = "شاہ رکن عالم و گھنٹہ گھر",
      hallmarkDescription = "Sacred domes of Multani blue kashi tilework and the historic Clock Tower stand.",
      hallmarkIconType = "shrine",
      defaultStandName = "Ghanta Ghar Chowk Stand",
      defaultStandAddress = "Ghanta Ghar / Clock Tower, Multan City",
      centerLat = 30.1984,
      centerLng = 71.4687,
      locations = listOf(
        LocationPoint("Ghanta Ghar Stand", "گھنٹہ گھر اسٹینڈ", "Stand", 30.1984, 71.4687, 0.0),
        LocationPoint("Nishtar Hospital & Medical College", "نشتر ہسپتال", "Hospital", 30.1872, 71.4429, 4.0),
        LocationPoint("Bahauddin Zakariya University (BZU)", "بہاؤالدین زکریا یونیورسٹی", "University", 30.2642, 71.5034, 10.0),
        LocationPoint("Multan Cantt Railway Station", "ملتان کینٹ اسٹیشن", "Transport", 30.1764, 71.4402, 3.5),
        LocationPoint("Hussain Agahi Bazar", "حسین آگاہی بازار", "Market", 30.1970, 71.4655, 1.8),
        LocationPoint("Mall of Multan (Bosan Road)", "مال آف ملتان", "Market", 30.2285, 71.4725, 5.0),
        LocationPoint("Chowk Kumharanwala", "چوک کمہاراں والا", "Transport", 30.2085, 71.5120, 7.0),
        LocationPoint("Shah Rukn-e-Alam Tomb & Qasim Fort", "مزار شاہ رکن عالم", "Landmark", 30.2005, 71.4715, 2.0),
        LocationPoint("Gulgasht Colony Gol Bagh", "گلگشت گول باغ", "Residential", 30.2241, 71.4882, 6.0),
        LocationPoint("Vehari Chowk General Bus Stand", "وہاڑی چوک بس اڈا", "Transport", 30.1650, 71.4920, 8.5),
        LocationPoint("Chungi No. 9", "چنگی نمبر 9", "Transport", 30.2120, 71.4650, 3.0),
        LocationPoint("Kachehri Chowk & High Court", "کچہری چوک", "Landmark", 30.1920, 71.4550, 2.5),
        LocationPoint("Bahauddin Zakariya Shrine", "مزار بہاؤالدین زکریا", "Landmark", 30.2010, 71.4720, 2.2),
        LocationPoint("Shah Shams Tabrez Tomb", "مزار شاہ شمس تبریز", "Landmark", 30.2115, 71.4810, 4.5),
        LocationPoint("Multan International Airport", "ملتان ائیرپورٹ", "Transport", 30.2030, 71.4190, 8.0)
      )
    ),

    // 2. LAHORE
    CityInfo(
      id = "lahore",
      name = "Lahore",
      urduName = "لاہور",
      tagLine = "Heart of Pakistan",
      hallmarkTitle = "Badshahi Mosque & Minar-e-Pakistan",
      hallmarkUrdu = "بادشاہی مسجد و مینارِ پاکستان",
      hallmarkDescription = "Magnificent Mughal red sandstone arches and the iconic monument of independence.",
      hallmarkIconType = "mosque",
      defaultStandName = "Liberty Roundabout Stand",
      defaultStandAddress = "Liberty Market, Gulberg III, Lahore",
      centerLat = 31.5204,
      centerLng = 74.3587,
      locations = listOf(
        LocationPoint("Liberty Market Stand (Gulberg)", "لبرٹی مارکیٹ اسٹینڈ", "Stand", 31.5110, 74.3440, 0.0),
        LocationPoint("Kalma Chowk Metro Station", "کلمہ چوک میٹرو", "Transport", 31.5035, 74.3315, 2.2),
        LocationPoint("Lahore Railway Station", "لاہور ریلوے اسٹیشن", "Transport", 31.5765, 74.3340, 9.0),
        LocationPoint("Badshahi Mosque & Shahi Qila", "بادشاہی مسجد و شاہی قلعہ", "Landmark", 31.5880, 74.3095, 11.5),
        LocationPoint("Minar-e-Pakistan", "مینارِ پاکستان", "Landmark", 31.5925, 74.3090, 12.0),
        LocationPoint("Allama Iqbal Airport (LHE)", "لاہور ائیرپورٹ", "Transport", 31.5215, 74.4035, 9.5),
        LocationPoint("Anarkali Bazar & Mall Road", "انارکلی بازار مال روڈ", "Market", 31.5645, 74.3140, 8.0),
        LocationPoint("DHA Y-Block Commercial", "ڈی ایچ اے وائی بلاک", "Market", 31.4720, 74.3820, 7.5),
        LocationPoint("Thokar Niaz Baig Adda", "ٹھوکر نیاز بیگ اڈا", "Transport", 31.4675, 74.2415, 12.5),
        LocationPoint("Model Town Central Park", "ماڈل ٹاؤن سینٹرل پارک", "Residential", 31.4880, 74.3210, 4.0),
        LocationPoint("Services Hospital (Jail Road)", "سروسز ہسپتال", "Hospital", 31.5360, 74.3380, 3.8),
        LocationPoint("Mayo Hospital Emergency", "میو ہسپتال", "Hospital", 31.5710, 74.3160, 8.5),
        LocationPoint("Punjab University (New Campus)", "پنجاب یونیورسٹی نیو کیمپس", "University", 31.5010, 74.2980, 5.5),
        LocationPoint("Lahore Ring Road Interchange", "لاہور رنگ روڈ", "Transport", 31.4420, 74.3910, 11.0),
        LocationPoint("Emporium Mall (Johar Town)", "ایمپوریئم مال جوہر ٹاؤن", "Market", 31.4680, 74.2670, 10.0),
        LocationPoint("Bahria Town Eiffel Tower", "بحریا ٹاؤن ایفل ٹاور", "Landmark", 31.3650, 74.1840, 24.0)
      )
    ),

    // 3. KARACHI
    CityInfo(
      id = "karachi",
      name = "Karachi",
      urduName = "کراچی",
      tagLine = "City of Lights",
      hallmarkTitle = "Mazar-e-Quaid & Clifton Coast",
      hallmarkUrdu = "مزارِ قائد و ساحلِ کلفٹن",
      hallmarkDescription = "Pristine white marble mausoleum of Quaid-e-Azam and the Arabian Sea coastline.",
      hallmarkIconType = "monument",
      defaultStandName = "Saddar Regal Chowk Stand",
      defaultStandAddress = "Regal Chowk, Saddar, Karachi",
      centerLat = 24.8607,
      centerLng = 67.0011,
      locations = listOf(
        LocationPoint("Saddar Regal Chowk Stand", "صدر ریگل چوک اسٹینڈ", "Stand", 24.8570, 67.0210, 0.0),
        LocationPoint("Mazar-e-Quaid (Jinnah Mausoleum)", "مزارِ قائد", "Landmark", 24.8745, 67.0395, 3.5),
        LocationPoint("Clifton Sea View Beach", "کلفٹن سی ویو", "Landmark", 24.7930, 67.0450, 8.5),
        LocationPoint("Karachi Cantt Railway Station", "کراچی کینٹ اسٹیشن", "Transport", 24.8480, 67.0340, 2.0),
        LocationPoint("Jinnah International Airport (KHI)", "جناح انٹرنیشنل ائیرپورٹ", "Transport", 24.9065, 67.1610, 16.5),
        LocationPoint("Jinnah Postgraduate Med Centre (JPMC)", "جناح ہسپتال کراچی", "Hospital", 24.8510, 67.0490, 3.8),
        LocationPoint("Aga Khan University Hospital", "آغا خان ہسپتال", "Hospital", 24.8920, 67.0740, 8.0),
        LocationPoint("Tariq Road Shopping District", "طارق روڈ مارکیٹ", "Market", 24.8710, 67.0610, 5.5),
        LocationPoint("Gulshan-e-Iqbal (NIPA Chowrangi)", "گلشنِ اقبال نیپا", "Market", 24.9190, 67.0980, 10.5),
        LocationPoint("North Nazimabad (Five Star Chowrangi)", "نارتھ ناظم آباد", "Residential", 24.9360, 67.0430, 11.0),
        LocationPoint("Karachi Port Trust (KPT)", "کراچی پورٹ ٹرسٹ", "Transport", 24.8290, 66.9810, 6.0),
        LocationPoint("Lucky One Mall (Rashid Minhas)", "لکی ون مال", "Market", 24.9450, 67.0910, 13.0),
        LocationPoint("University of Karachi (Main Gate)", "جامعہ کراچی", "University", 24.9420, 67.1190, 14.5),
        LocationPoint("Korangi Crossing Adda", "کورنگی کراسنگ", "Transport", 24.8210, 67.1120, 12.0),
        LocationPoint("Bahria Town Karachi Gate", "بحریا ٹاؤن کراچی", "Residential", 25.0120, 67.3210, 38.0)
      )
    ),

    // 4. ISLAMABAD
    CityInfo(
      id = "islamabad",
      name = "Islamabad",
      urduName = "اسلام آباد",
      tagLine = "The Beautiful Capital",
      hallmarkTitle = "Faisal Mosque & Margalla Hills",
      hallmarkUrdu = "فیصل مسجد و مارگلہ ہلز",
      hallmarkDescription = "World-famous Ottoman-Bedouin modernist mosque set against the lush Margalla range.",
      hallmarkIconType = "mosque",
      defaultStandName = "Blue Area Centaurus Stand",
      defaultStandAddress = "Blue Area, Jinnah Avenue, F-8/G-8, Islamabad",
      centerLat = 33.6844,
      centerLng = 73.0479,
      locations = listOf(
        LocationPoint("Centaurus / Blue Area Stand", "سینٹورس بلیو ایریا اسٹینڈ", "Stand", 33.7075, 73.0515, 0.0),
        LocationPoint("Faisal Mosque", "فیصل مسجد", "Landmark", 33.7295, 73.0375, 3.8),
        LocationPoint("Daman-e-Koh Margalla Viewpoint", "دامنِ کوہ", "Landmark", 33.7480, 73.0580, 7.5),
        LocationPoint("F-6 / Super Market", "ایف 6 سپر مارکیٹ", "Market", 33.7280, 73.0780, 4.2),
        LocationPoint("F-7 / Jinnah Super Market", "ایف 7 جناح سپر", "Market", 33.7210, 73.0570, 2.5),
        LocationPoint("F-10 Markaz", "ایف 10 مرکز", "Market", 33.6920, 73.0120, 4.8),
        LocationPoint("G-9 Markaz (Karachi Company)", "جی 9 کراچی کمپنی", "Market", 33.6890, 73.0320, 3.0),
        LocationPoint("I-8 Markaz", "آئی 8 مرکز", "Market", 33.6690, 73.0760, 6.0),
        LocationPoint("PIMS Hospital Emergency", "پمز ہسپتال اسلام آباد", "Hospital", 33.7020, 73.0480, 1.2),
        LocationPoint("Shifa International Hospital (H-8)", "شفا انٹرنیشنل ہسپتال", "Hospital", 33.6780, 73.0790, 5.5),
        LocationPoint("NUST University (H-12)", "نسٹ یونیورسٹی", "University", 33.6420, 72.9910, 11.5),
        LocationPoint("Quaid-i-Azam University (QAU)", "قائد اعظم یونیورسٹی", "University", 33.7480, 73.1360, 12.0),
        LocationPoint("Faizabad Metro Interchange", "فیض آباد میٹرو", "Transport", 33.6620, 73.0840, 7.0),
        LocationPoint("Lake View Park (Rawal Lake)", "لیک ویو پارک", "Landmark", 33.7120, 73.1250, 9.0),
        LocationPoint("Islamabad International Airport (IIAP)", "اسلام آباد ائیرپورٹ", "Transport", 33.5600, 72.8510, 28.0)
      )
    ),

    // 5. RAWALPINDI
    CityInfo(
      id = "rawalpindi",
      name = "Rawalpindi",
      urduName = "راولپنڈی",
      tagLine = "Historic Garrison City",
      hallmarkTitle = "Historic Haveli & Saddar Clock",
      hallmarkUrdu = "صدر گھنٹہ گھر و تاریخی حویلی",
      hallmarkDescription = "Bustling bazaars, garrison heritage, and gateway to northern mountains.",
      hallmarkIconType = "tower",
      defaultStandName = "Saddar Metro Station Stand",
      defaultStandAddress = "Mall Road, Saddar, Rawalpindi",
      centerLat = 33.5973,
      centerLng = 73.0479,
      locations = listOf(
        LocationPoint("Saddar Metro Station Stand", "صدر میٹرو اسٹینڈ پنڈی", "Stand", 33.5970, 73.0540, 0.0),
        LocationPoint("Rawalpindi Railway Station", "راولپنڈی ریلوے اسٹیشن", "Transport", 33.6030, 73.0640, 1.5),
        LocationPoint("Raja Bazar (Fawwara Chowk)", "راجہ بازار فوارہ چوک", "Market", 33.6150, 73.0570, 2.5),
        LocationPoint("Commercial Market (Satellite Town)", "کمرشل مارکیٹ سیٹلائٹ ٹاؤن", "Market", 33.6380, 73.0720, 5.5),
        LocationPoint("Benazir Bhutto Hospital (BBH)", "بینظیر بھٹو ہسپتال مری روڈ", "Hospital", 33.6260, 73.0710, 4.0),
        LocationPoint("Holy Family Hospital", "ہولی فیملی ہسپتال", "Hospital", 33.6390, 73.0640, 5.2),
        LocationPoint("Ayub National Park", "ایوب نیشنل پارک", "Landmark", 33.5620, 73.0860, 6.0),
        LocationPoint("Faizabad Bus Terminal", "فیض آباد بس ٹرمینل", "Transport", 33.6620, 73.0840, 8.5),
        LocationPoint("Pirwadhai General Bus Stand", "پیرودھائی بس اڈا", "Transport", 33.6380, 73.0320, 5.8),
        LocationPoint("Bahria Town Phase 1-6 Gate", "بحریا ٹاؤن پنڈی گیٹ", "Residential", 33.5280, 73.1110, 12.0),
        LocationPoint("DHA Phase 1 (GT Road)", "ڈی ایچ اے فیز 1", "Residential", 33.5350, 73.1020, 10.5),
        LocationPoint("Chaklala Railway Station", "چکلالہ اسٹیشن", "Transport", 33.5890, 73.0890, 4.5)
      )
    ),

    // 6. FAISALABAD
    CityInfo(
      id = "faisalabad",
      name = "Faisalabad",
      urduName = "فیصل آباد",
      tagLine = "Manchester of Pakistan",
      hallmarkTitle = "Eight-Bazar Victorian Clock Tower",
      hallmarkUrdu = "آٹھ بازاروں کا تاریخی گھنٹہ گھر",
      hallmarkDescription = "Union Jack radial layout of 8 historic markets centered on the iconic Victorian Ghanta Ghar.",
      hallmarkIconType = "tower",
      defaultStandName = "Ghanta Ghar Central Stand",
      defaultStandAddress = "Clock Tower Chowk, Faisalabad",
      centerLat = 31.4187,
      centerLng = 73.0791,
      locations = listOf(
        LocationPoint("Ghanta Ghar Central Stand", "گھنٹہ گھر فیصل آباد اسٹینڈ", "Stand", 31.4187, 73.0791, 0.0),
        LocationPoint("D-Ground Commercial Center", "ڈی گراؤنڈ کمرشل", "Market", 31.4075, 73.1040, 3.5),
        LocationPoint("Kohinoor City (Jaranwala Rd)", "کوہِ نور سٹی", "Market", 31.4140, 73.1180, 4.8),
        LocationPoint("Faisalabad Railway Station", "فیصل آباد ریلوے اسٹیشن", "Transport", 31.4285, 73.0890, 1.8),
        LocationPoint("Allied Hospital Emergency", "الائیڈ ہسپتال فیصل آباد", "Hospital", 31.4410, 73.0720, 3.2),
        LocationPoint("DHQ Hospital (Hospital Road)", "ڈی ایچ کیو ہسپتال", "Hospital", 31.4140, 73.0870, 1.0),
        LocationPoint("University of Agriculture (UAF)", "زرعی یونیورسٹی فیصل آباد", "University", 31.4320, 73.0690, 2.5),
        LocationPoint("National Textile University (NTU)", "نیشنل ٹیکسٹائل یونیورسٹی", "University", 31.4650, 73.1550, 9.5),
        LocationPoint("General Bus Stand (Rajbah Rd)", "جنرل بس اسٹینڈ راجباہ روڈ", "Transport", 31.4260, 73.1010, 2.8),
        LocationPoint("Peoples Colony No. 1", "پیپلز کالونی 1", "Residential", 31.4020, 73.1090, 4.0),
        LocationPoint("Madina Town (Susan Road)", "مدینہ ٹاؤن سوسن روڈ", "Residential", 31.4290, 73.1250, 5.5),
        LocationPoint("Gatwala Wildlife Park", "گٹ والا پارک", "Landmark", 31.5020, 73.1890, 14.0)
      )
    ),

    // 7. PESHAWAR
    CityInfo(
      id = "peshawar",
      name = "Peshawar",
      urduName = "پشاور",
      tagLine = "City of Flowers & Khyber Hospitality",
      hallmarkTitle = "Bab-e-Khyber & Qissa Khwani Arch",
      hallmarkUrdu = "بابِ خیبر و قصہ خوانی بازار",
      hallmarkDescription = "Legendary gateway of Central Asian caravans and historic storytelling avenues.",
      hallmarkIconType = "fort",
      defaultStandName = "Saddar Gora Bazar Stand",
      defaultStandAddress = "Saddar Road, Peshawar Cantt",
      centerLat = 34.0151,
      centerLng = 71.5249,
      locations = listOf(
        LocationPoint("Saddar Road Cantt Stand", "صدر روڈ کینٹ پشاور اسٹینڈ", "Stand", 34.0080, 71.5380, 0.0),
        LocationPoint("Qissa Khwani Bazar", "قصہ خوانی بازار", "Market", 34.0110, 71.5720, 3.8),
        LocationPoint("Bala Hissar Fort", "قلعہ بالاحصار", "Landmark", 34.0160, 71.5670, 3.2),
        LocationPoint("Khyber Teaching Hospital (KTH)", "خیبر ٹیچنگ ہسپتال", "Hospital", 33.9980, 71.4820, 6.5),
        LocationPoint("Lady Reading Hospital (LRH)", "لیڈی ریڈنگ ہسپتال", "Hospital", 34.0140, 71.5760, 4.2),
        LocationPoint("University of Peshawar", "پشاور یونیورسٹی", "University", 33.9990, 71.4780, 7.0),
        LocationPoint("Hayatabad Phase 3 Chowk", "حیات آباد فیز 3", "Residential", 33.9850, 71.4390, 11.5),
        LocationPoint("Peshawar Cantt Railway Station", "پشاور کینٹ ریلوے اسٹیشن", "Transport", 34.0040, 71.5420, 1.0),
        LocationPoint("General Bus Stand (Haji Camp)", "حاجی کیمپ بس اڈا", "Transport", 34.0240, 71.6020, 7.5),
        LocationPoint("Karkhano Market (Jamrud Road)", "کارخانو مارکیٹ", "Market", 33.9910, 71.4180, 13.5),
        LocationPoint("Bab-e-Khyber (Jamrud Gateway)", "بابِ خیبر جمرود", "Landmark", 33.9980, 71.3850, 17.0)
      )
    ),

    // 8. QUETTA
    CityInfo(
      id = "quetta",
      name = "Quetta",
      urduName = "کوئٹہ",
      tagLine = "Fruit Garden of Pakistan",
      hallmarkTitle = "Chiltan Peaks & Ziarat Juniper Crest",
      hallmarkUrdu = "کوہِ چلتن و زیارت صنوبر",
      hallmarkDescription = "Surrounded by majestic dry mountain passes and sweet orchard aromas.",
      hallmarkIconType = "hills",
      defaultStandName = "Liaquat Bazar Chowk Stand",
      defaultStandAddress = "Liaquat Bazar, Quetta City",
      centerLat = 30.1798,
      centerLng = 66.9750,
      locations = listOf(
        LocationPoint("Liaquat Bazar Stand", "لیاقت بازار کوئٹہ اسٹینڈ", "Stand", 30.1810, 66.9980, 0.0),
        LocationPoint("Jinnah Road Commercial", "جناح روڈ کوئٹہ", "Market", 30.1880, 67.0050, 1.2),
        LocationPoint("Quetta Railway Station", "کوئٹہ ریلوے اسٹیشن", "Transport", 30.1980, 67.0120, 2.5),
        LocationPoint("Sandeman Civil Hospital", "سول ہسپتال کوئٹہ", "Hospital", 30.1840, 66.9990, 0.8),
        LocationPoint("Bolan Medical College (BMC)", "بولان میڈیکل کالج", "Hospital", 30.1580, 66.9780, 3.8),
        LocationPoint("University of Balochistan (Sariab)", "بلوچستان یونیورسٹی سریاب روڈ", "University", 30.1510, 66.9850, 4.5),
        LocationPoint("Hanna Lake Park", "ہنہ جھیل کوئٹہ", "Landmark", 30.2520, 67.1010, 14.0),
        LocationPoint("Quetta Cantt Checkpost", "کوئٹہ کینٹ", "Transport", 30.2080, 67.0320, 4.2),
        LocationPoint("Hazara Town", "ہزارہ ٹاؤن", "Residential", 30.1450, 66.9450, 7.5)
      )
    ),

    // 9. BAHAWALPUR
    CityInfo(
      id = "bahawalpur",
      name = "Bahawalpur",
      urduName = "بہاولپور",
      tagLine = "Land of Nawabs & Palaces",
      hallmarkTitle = "Noor Mahal & Farid Gate",
      hallmarkUrdu = "نور محل و فرید گیٹ",
      hallmarkDescription = "Opulent royal Italianate palace architecture and Cholistan heritage.",
      hallmarkIconType = "palace",
      defaultStandName = "Farid Gate Central Stand",
      defaultStandAddress = "Farid Gate, Circular Road, Bahawalpur",
      centerLat = 29.3544,
      centerLng = 71.6911,
      locations = listOf(
        LocationPoint("Farid Gate Central Stand", "فرید گیٹ بہاولپور اسٹینڈ", "Stand", 29.3950, 71.6830, 0.0),
        LocationPoint("Noor Mahal Royal Palace", "نور محل شاہی محل", "Landmark", 29.3850, 71.6680, 2.8),
        LocationPoint("Bahawal Victoria Hospital (BVH)", "بہاول وکٹوریہ ہسپتال", "Hospital", 29.3910, 71.6770, 1.2),
        LocationPoint("Bahawalpur Railway Station", "بہاولپور ریلوے اسٹیشن", "Transport", 29.3790, 71.6920, 2.5),
        LocationPoint("The Islamia University (Baghdad Campus)", "اسلامیہ یونیورسٹی بغداد کیمپس", "University", 29.3780, 71.7610, 9.5),
        LocationPoint("Bahawalpur Zoo & Museum", "بہاولپور چڑیا گھر و میوزیم", "Landmark", 29.3970, 71.6890, 1.0),
        LocationPoint("One Unit Chowk (Model Town A)", "ون یونٹ چوک ماڈل ٹاؤن", "Residential", 29.3880, 71.7010, 3.2),
        LocationPoint("General Bus Stand (Lari Adda)", "جنرل بس اڈا بہاولپور", "Transport", 29.4050, 71.6950, 2.0),
        LocationPoint("Derawar Fort (Cholistan Edge)", "قلعہ دراوڑ چولستان", "Landmark", 28.7690, 71.3340, 75.0)
      )
    ),

    // 10. GUJRANWALA
    CityInfo(
      id = "gujranwala",
      name = "Gujranwala",
      urduName = "گوجرانوالہ",
      tagLine = "City of Wrestlers & Delicacies",
      hallmarkTitle = "Rustom-e-Hind Wrestler Crest",
      hallmarkUrdu = "رستمِ ہند پہلوانی نشان",
      hallmarkDescription = "Historic martial vigor, traditional chirgha feasts, and G.T. Road industries.",
      hallmarkIconType = "monument",
      defaultStandName = "Pindi Bypass Chowk Stand",
      defaultStandAddress = "G.T Road, Pindi Bypass, Gujranwala",
      centerLat = 32.1877,
      centerLng = 74.1945,
      locations = listOf(
        LocationPoint("Pindi Bypass Chowk Stand", "پنڈی بائی پاس اسٹینڈ", "Stand", 32.1890, 74.1850, 0.0),
        LocationPoint("Model Town Market", "ماڈل ٹاؤن مارکیٹ", "Market", 32.1760, 74.1950, 2.2),
        LocationPoint("Gujranwala Cantt Gate", "گوجرانوالہ کینٹ", "Transport", 32.2280, 74.1750, 5.5),
        LocationPoint("Civil Hospital Emergency", "سول ہسپتال گوجرانوالہ", "Hospital", 32.1580, 74.1820, 4.0),
        LocationPoint("DHQ Hospital Gujranwala", "ڈی ایچ کیو ہسپتال", "Hospital", 32.1620, 74.1890, 3.5),
        LocationPoint("General Bus Stand (G.T Road)", "جنرل بس اسٹینڈ جی ٹی روڈ", "Transport", 32.1680, 74.1940, 2.8),
        LocationPoint("DC Colony Gate", "ڈی سی کالونی", "Residential", 32.2510, 74.1680, 8.5),
        LocationPoint("Satellite Town Commercial", "سیٹلائٹ ٹاؤن مارکیٹ", "Market", 32.1720, 74.2080, 3.0),
        LocationPoint("Chanda Qila Bypass", "چندا قلعہ بائی پاس", "Transport", 32.1380, 74.2150, 7.0)
      )
    ),

    // 11. SIALKOT
    CityInfo(
      id = "sialkot",
      name = "Sialkot",
      urduName = "سیالکوٹ",
      tagLine = "Global Export Capital & Iqbal Heritage",
      hallmarkTitle = "Iqbal Manzil & Crafted Star",
      hallmarkUrdu = "اقبال منزل و دستکاری نشان",
      hallmarkDescription = "Birthplace of poet Allama Iqbal and world-leading sports and surgical craftsmanship.",
      hallmarkIconType = "monument",
      defaultStandName = "Kachehri Road Cantt Stand",
      defaultStandAddress = "Kachehri Road, Cantt, Sialkot",
      centerLat = 32.4945,
      centerLng = 74.5229,
      locations = listOf(
        LocationPoint("Kachehri Road Cantt Stand", "کچہری روڈ کینٹ اسٹینڈ", "Stand", 32.4980, 74.5310, 0.0),
        LocationPoint("Iqbal Manzil Historical Site", "اقبال منزل سیالکوٹ", "Landmark", 32.4920, 74.5390, 1.5),
        LocationPoint("Sialkot International Airport (SKT)", "سیالکوٹ ائیرپورٹ", "Transport", 32.5360, 74.3640, 18.0),
        LocationPoint("Allama Iqbal Memorial Hospital", "علامہ اقبال میموریل ہسپتال", "Hospital", 32.4890, 74.5350, 1.8),
        LocationPoint("Sialkot Railway Station", "سیالکوٹ ریلوے اسٹیشن", "Transport", 32.4860, 74.5420, 2.2),
        LocationPoint("Paris Road Shopping Hub", "پیرس روڈ مارکیٹ", "Market", 32.5020, 74.5260, 1.2),
        LocationPoint("Small Industrial Estate", "سمال انڈسٹریل اسٹیٹ", "Market", 32.5180, 74.5480, 3.8),
        LocationPoint("Sialkot Clock Tower (Saddar)", "گھنٹہ گھر صدر سیالکوٹ", "Landmark", 32.4960, 74.5340, 0.8),
        LocationPoint("Sambrial Dry Port Adda", "سمبڑیال ڈرائی پورٹ", "Transport", 32.4850, 74.3520, 17.5)
      )
    ),

    // 12. HYDERABAD
    CityInfo(
      id = "hyderabad",
      name = "Hyderabad",
      urduName = "حیدرآباد",
      tagLine = "City of Windcatchers & Perfumes",
      hallmarkTitle = "Paccho Qila & Auto Bhan Gate",
      hallmarkUrdu = "پکا قلعہ و آٹو بھان گیٹ",
      hallmarkDescription = "Historic Kalhora & Talpur forts, fragrant glass bangle souks, and Indus breezes.",
      hallmarkIconType = "fort",
      defaultStandName = "Auto Bhan Road Stand",
      defaultStandAddress = "Auto Bhan Road, Latifabad, Hyderabad",
      centerLat = 25.3960,
      centerLng = 68.3578,
      locations = listOf(
        LocationPoint("Auto Bhan Road Stand", "آٹو بھان روڈ اسٹینڈ", "Stand", 25.3780, 68.3490, 0.0),
        LocationPoint("Pakka Qila Historical Fort", "پکا قلعہ حیدرآباد", "Landmark", 25.3920, 68.3710, 3.5),
        LocationPoint("Resham Gali & Shahi Bazar", "ریشم گلی شاہی بازار", "Market", 25.3960, 68.3680, 3.8),
        LocationPoint("Civil Hospital Hyderabad (LUMHS)", "سول ہسپتال حیدرآباد", "Hospital", 25.3910, 68.3620, 3.0),
        LocationPoint("Hyderabad Railway Station", "حیدرآباد ریلوے اسٹیشن", "Transport", 25.3880, 68.3750, 4.0),
        LocationPoint("Sindh University Jamshoro Gate", "سندھ یونیورسٹی جامشورو", "University", 25.4290, 68.2610, 13.5),
        LocationPoint("Latifabad Unit No. 7 Market", "لطیف آباد یونٹ 7", "Residential", 25.3650, 68.3580, 2.5),
        LocationPoint("Qasimabad Nasim Nagar Chowk", "قاسم آباد نسیم نگر", "Residential", 25.4080, 68.3320, 4.8),
        LocationPoint("Badin Bus Stop (Haider Chowk)", "حیدر چوک بس اسٹاپ", "Transport", 25.3930, 71.3650, 3.2)
      )
    )
  )

  fun getCityById(id: String, customCities: List<CustomCity> = emptyList()): CityInfo {
    val builtin = cities.find { it.id.equals(id, ignoreCase = true) }
    if (builtin != null) return builtin

    val custom = customCities.find { it.id.equals(id, ignoreCase = true) }
    if (custom != null) {
      return CityInfo(
        id = custom.id,
        name = custom.name,
        urduName = custom.urduName.ifBlank { custom.name },
        tagLine = custom.tagLine,
        hallmarkTitle = custom.hallmarkTitle,
        hallmarkUrdu = custom.hallmarkUrdu,
        hallmarkDescription = custom.hallmarkDescription,
        hallmarkIconType = custom.hallmarkIconType,
        defaultStandName = custom.defaultStandName,
        defaultStandAddress = custom.defaultStandAddress,
        centerLat = custom.centerLat,
        centerLng = custom.centerLng,
        locations = listOf(
          LocationPoint(
            name = custom.defaultStandName,
            urduName = custom.urduName,
            category = "Stand",
            lat = custom.centerLat,
            lng = custom.centerLng,
            approxDistanceKmFromStand = 0.0
          )
        )
      )
    }
    return cities[0]
  }

  fun getAllCities(customCities: List<CustomCity> = emptyList()): List<CityInfo> {
    val customs = customCities.map { custom ->
      CityInfo(
        id = custom.id,
        name = custom.name,
        urduName = custom.urduName.ifBlank { custom.name },
        tagLine = custom.tagLine,
        hallmarkTitle = custom.hallmarkTitle,
        hallmarkUrdu = custom.hallmarkUrdu,
        hallmarkDescription = custom.hallmarkDescription,
        hallmarkIconType = custom.hallmarkIconType,
        defaultStandName = custom.defaultStandName,
        defaultStandAddress = custom.defaultStandAddress,
        centerLat = custom.centerLat,
        centerLng = custom.centerLng,
        locations = listOf(
          LocationPoint(
            name = custom.defaultStandName,
            urduName = custom.urduName,
            category = "Stand",
            lat = custom.centerLat,
            lng = custom.centerLng,
            approxDistanceKmFromStand = 0.0
          )
        )
      )
    }
    return cities + customs
  }

  fun getCombinedCityLocations(
    city: CityInfo,
    customDestinations: List<CityDestination>
  ): List<LocationPoint> {
    val customPoints = customDestinations
      .filter { it.cityId.equals(city.id, ignoreCase = true) }
      .map { cd ->
        LocationPoint(
          name = cd.name,
          urduName = cd.urduName,
          category = cd.category,
          lat = if (cd.lat != 0.0) cd.lat else city.centerLat,
          lng = if (cd.lng != 0.0) cd.lng else city.centerLng,
          approxDistanceKmFromStand = cd.approxDistanceKm
        )
      }
    // Deduplicate by name
    val existingNames = city.locations.map { it.name.trim().lowercase() }.toSet()
    val nonDuplicateCustom = customPoints.filter { it.name.trim().lowercase() !in existingNames }
    return city.locations + nonDuplicateCustom
  }

  fun calculateAccurateDistance(pickup: LocationPoint, drop: LocationPoint): Double {
    return calculateHaversineRoadDistance(pickup.lat, pickup.lng, drop.lat, drop.lng)
  }

  fun calculateHaversineRoadDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    if (lat1 == lat2 && lon1 == lon2) return 0.0
    val earthRadiusKm = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
      cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
      sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    val straightDist = earthRadiusKm * c
    // Urban road winding correction multiplier for realistic travel in Pakistan cities
    val roadDist = straightDist * 1.28
    return max(0.5, round(roadDist * 10.0) / 10.0)
  }
}


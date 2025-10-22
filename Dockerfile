# --- 1. AŞAMA: İNŞA ETME (Builder) ---
# Java 21 ve Maven 3.9 içeren hazır bir imajı temel alıyoruz. Bu aşamaya "builder" adını veriyoruz.
FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder

# Konteynerin içinde /app adında bir çalışma dizini oluşturuyoruz.
WORKDIR /app

# Önce sadece pom.xml'i kopyalıyoruz.
# Bu bir optimizasyon tekniğidir. Bağımlılıklar değişmediği sürece Docker bu adımı tekrar çalıştırmaz, önbellekten kullanır.
COPY pom.xml .

# pom.xml'deki tüm kütüphaneleri (bağımlılıkları) indiriyoruz.
RUN mvn dependency:go-offline

# Şimdi projenin geri kalan kaynak kodunu kopyalıyoruz.
COPY src ./src

# Maven ile projeyi paketliyoruz (derleyip .jar dosyası oluşturuyoruz). Testleri atlıyoruz çünkü burada amacımız sadece derlemek.
RUN mvn clean package -DskipTests


# --- 2. AŞAMA: ÇALIŞTIRMA (Final Image) ---
# Sadece Java Runtime Environment (JRE) içeren, çok daha küçük boyutlu bir imajı temel alıyoruz.
FROM eclipse-temurin:21-jre-jammy

# Konteyner içinde /app çalışma dizinini oluşturuyoruz.
WORKDIR /app

# Bir önceki "builder" aşamasında, /app/target/ dizininde oluşturulan .jar dosyasını bu yeni aşamaya kopyalıyoruz ve adını app.jar yapıyoruz.
COPY --from=builder /app/target/*.jar app.jar

# Docker'a bu konteynerin 8083 portunu dışarıya açacağını bildiriyoruz.
EXPOSE 8083

# Konteyner başlatıldığında çalıştırılacak olan ana komut. Bizim app.jar dosyamızı çalıştırır.
ENTRYPOINT ["java", "-jar", "app.jar"]
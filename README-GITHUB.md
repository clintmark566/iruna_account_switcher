# Upload ke GitHub lalu build APK dari HP

## 1. Upload semua isi folder ini
Upload seluruh isi ZIP ke repository `iruna-account-switcher`.
Pastikan `.github/workflows/build-apk.yml` ikut ter-upload.

## 2. Jalankan build
Di GitHub:
- buka tab **Actions**
- pilih **Build APK**
- tekan **Run workflow**
- tunggu sampai workflow selesai

## 3. Download APK
Buka workflow yang selesai, lalu bagian **Artifacts**.
Download `IrunaAccountSwitcher-debug`.

## Catatan
- APK ini adalah debug APK untuk pemakaian pribadi/testing.
- Slot 1 dikonfigurasi ke `clintmark566@gmail.com`.
- Password Google tidak disimpan.
- Akun Google harus sudah ditambahkan ke perangkat Android.
- Jika GitHub menolak workflow karena repository baru, pastikan Actions diaktifkan pada Settings > Actions.

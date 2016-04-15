# Tiny Book Reader

読書画面の実装デモです。書籍のレンダリングエンジンとしては [PdfRenderer](http://developer.android.com/intl/ja/reference/android/graphics/pdf/PdfRenderer.html) を使っています。

Deploygateによるでもアプリ配信: https://dply.me/xntdx6

例として使っているPDFは 『Androidアプリのセキュア設計・セキュアコーディングガイド』です。

https://www.jssec.org/report/securecoding.html

## 実装済み機能

* Landscape時の見開き
* 見開きでも表紙は1ページだけでセンタリングする
* 画面回転時の読書位置の保存
* ページロード時の非同期画像読み込み ＋ふわっと表示
* おまけ: OkHttpを使ってダウンロードするときのProgressBar処理

## License

Copyright (c) 2015 FUJI Goro (gfx).

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
var gulp = require("gulp");
var sass = require("gulp-sass");
var uglify = require("gulp-uglify");
var jade = require('gulp-jade');
var minifyCSS = require('gulp-minify-css');
var connect = require('gulp-connect');



var OUT_PUT_PATH = "../main/resources/";

/** sass設定*/
gulp.task("sass", function() {
    gulp.src("src/sass/**/*.scss")
        .pipe(sass())
        .pipe(gulp.dest(OUT_PUT_PATH + "css"));
});


gulp.task("js", function() {
    gulp.src(["src/js/**/*.js","!src/js/min/**/*.js"])
        .pipe(uglify())
        .pipe(gulp.dest(OUT_PUT_PATH + "js"));
});

//テンプレートファイル確認用
gulp.task('jade', function () {
    gulp.src(['src/jade/**/*.jade',"!src/jade/**/_*.jade"])      // gulp.src でファイルを指定
        .pipe(jade())
        .pipe(gulp.dest('./out/'))
});



gulp.task('lib', function() {
    gulp.src('./src/sass/**/*css')
        .pipe(minifyCSS({keepBreaks:true}))
        .pipe(gulp.dest(OUT_PUT_PATH + "css"))
});

gulp.task('default', ['js','sass']);

<?php
//设置数据库变量
$db_host   = 'localhost';  //数据库主机名称，一般都为localhost
$db_user   = 'root';        //数据库用户帐号，根据个人情况而定
$db_passw = 'root';   //数据库用户密码，根据个人情况而定
$db_name  = 'word_test';         //数据库具体名称，以刚才创建的数据库为准
//连接数据库
$conn = mysql_connect($db_host,$db_user,$db_passw) or die ('数据库连接失败！');
//设置字符集，如utf8和gbk等，根据数据库的字符集而定
mysql_query("set character set 'utf8_bin'");
//选定数据库
mysql_select_db($db_name,$conn) or die('数据库选定失败！'); 
//执行SQL语句(查询)
//$result = mysql_query($sql) or die('数据库查询失败！');
?>



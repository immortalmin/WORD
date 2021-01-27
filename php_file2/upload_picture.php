<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php';
if(empty($_FILES)){
	echo "无上传文件";
}else{
	$dirPath = '../img/profile/';
	if(!is_dir($dirPath)){
		echo "创建目录";
		@mkdir($dirPath);
	}
	$file = $_FILES['image'];
	if(is_uploaded_file($file['tmp_name'])){
		$info = pathinfo($_FILES['image']['name']);
		$ext = $info['extension'];
		$uid = $_POST['uid'];
		if(move_uploaded_file($_FILES['image']['tmp_name'],$dirPath.$uid.'.'.$ext)){
			mysqli_query($conn,'UPDATE user SET profile_photo="'.$uid.'.'.$ext.'" WHERE uid='.$uid);
			echo "upload successful";
		}else{
			echo "failure";
		}
		
	}else{
		echo "文件不合法";
	}

}
mysqli_close($conn);
?>


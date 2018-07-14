package com.hochoy.test;//这部分是根据项目特定需要将/home 逻辑卷和分区删除的操作步骤，这部分内容根据实际需要进行操作
//centos6.5系统装好之后，需要系统进行分区，根据需要将/home所在分区与根目录所在分区放一个分区，及将/home下的空间释放到根目录，
//
//并将/home 所在分区的逻辑卷删除，操作步骤如下：
//
//卸载/home并删除逻辑卷home （如果/home 下有需要备份的务必先备份， ）
//
//# umount /home
//# df -h //查看磁盘情况
//
//# lvremove /dev/mapper/VolGroup-lv_home  //删除逻辑卷home
//
//# vgdisplay //查看卷组可用空间
//
//# lvextend -L +200GiB /dev/mapper/VolGroup-lv_root #给根分区增加1380G空间
//
//# resize2fs -p /dev/mapper/VolGroup-lv_root #这个名字就是重新调整大小，执行时间较长，要耐心等待
//
//以下步骤非常关键，删除home分区之后一定要将/etc/fstab 中对应的已经删除的分区部分内容删掉，
//本人此部分第一次没做，最后重装了系统（有好的解决方案的可以分享一下）
//
//

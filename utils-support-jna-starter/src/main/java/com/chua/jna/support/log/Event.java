package com.chua.jna.support.log;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 事件
 *事件id对应表如下所示
 *<table  bgcolor="white">
 *     <thead>
 *         <tr >
 *             <th bgcolor="red">id</th>
 *             <th bgcolor="red">类型</th>
 *             <th bgcolor="red">来源</th>
 *             <th bgcolor="red">代 表 的 意 义 举 例 解 释</th>
 *         </tr>
 *     </thead>
 *     <tbody>
 *         <tr>
 *             <td bgcolor="gray">2</td>
 *             <td bgcolor="gray">信息</td>
 *             <td bgcolor="gray">Serial</td>
 *             <td bgcolor="gray">在验证 \Device\Serial1 是否确实是串行口时，系统检测到先进先出方式(fifo)。将使用该方式。</td>
 *         </tr>
 *
 *          <tr>
 *              <td bgcolor="gray">17</td>
 *              <td bgcolor="gray">错误</td>
 *              <td bgcolor="gray">W32Time</td>
 *              <td bgcolor="gray">时间提供程序 NtpClient: 在 DNS 查询手动配置的对等机器 ‘time.windows.com,0x1’ 时发生一个错误。 NtpClient 将在 15 分钟内重试 NDS 查询。 错误为: 套接字操作尝试一个无法连接的主机。 (0x80072751)</td>
 *          </tr>
 *
 *          <tr>
 *              <td bgcolor="gray">20</td>
 *              <td bgcolor="gray">警告</td>
 *              <td bgcolor="gray">Print</td>
 *              <td bgcolor="gray">已经添加或更新 Windows NT x86 Version-3 的打印机驱动程序 Canon PIXMA iP1000。文件:- CNMDR6e.DLL, CNMUI6e.DLL, CNMCP6e.DLL, CNMMH6e.HLP, CNMD56e.DLL, CNMUR6e.DLL, CNMSR6e.DLL, CNMIN6e.INI, CNMPI6e.DLL, CNMSM6e.EXE, CNMSS6e.SMR, CNMSD6e.EXE, CNMSQ6e.EXE, CNMSH6e.HLP, CNMSH6e
 *              </td>
 *          </tr>
 *
 *          <tr>
 *              <td bgcolor="gray">26</td>
 *              <td bgcolor="gray">信息</td>
 *              <td bgcolor="gray">Application Popup</td>
 *              <td bgcolor="gray">
 *                  弹出应用程序: Rsaupd.exe - 无法找到组件: 没有找到 MFC71.DLL，因此这个应用程序未能启动。重新安装应用程序可能会修复此问题。
 *              </td>
 *          </tr>
 *
 *          <tr>
 *              <td bgcolor="gray">29</td>
 *              <td bgcolor="gray">错误</td>
 *              <td bgcolor="gray">W32Time</td>
 *              <td bgcolor="gray">
 *                  时间服务提供程序 NtpClient 配置为从一个或多个时间源 获得时间，但是，没有一个源可以访问。在 14 分钟内不 会进行联系时间源的尝试。 NtpClient 没有准确时间的时间源。
 *              </td>
 *          </tr>
 *
 *          <tr>
 *              <td bgcolor="gray">35</td>
 *              <td bgcolor="gray">信息</td>
 *              <td bgcolor="gray">W32Time</td>
 *              <td bgcolor="gray">
 *                  时间服务现在用时间源 time.windows.com (ntp.m
 *              </td>
 *          </tr>
 *
 *          <tr>
 *              <td bgcolor="gray">115</td>
 *              <td bgcolor="gray">信息</td>
 *              <td bgcolor="gray">SRService</td>
 *              <td bgcolor="gray">
 *                  系统还原监视在所有驱动器上启用
 *              </td>
 *          </tr>
 *
 *          <tr>
 *              <td bgcolor="gray">116</td>
 *              <td bgcolor="gray">信息</td>
 *              <td bgcolor="gray">SRService</td>
 *              <td bgcolor="gray">
 *                  系统还原监视在所有驱动器上禁用
 *              </td>
 *          </tr>
 *
 *          <tr>
 *              <td bgcolor="gray">1001</td>
 *              <td bgcolor="gray">信息</td>
 *              <td bgcolor="gray">Save Dump</td>
 *              <td bgcolor="gray">
 *                  计算机已经从检测错误后重新启动。检测错误: 0x4a4b4d53 (0xc000000e, 0x01d04bf0, 0x00000010, 0x0000029a)。 已将转储的数据保存在: C:\WINDOWS\Minidump\Mini052809-01.dmp。
 *              </td>
 *          </tr>
 *
 *          <tr>
 *              <td bgcolor="gray">1005</td>
 *              <td bgcolor="gray">警告</td>
 *              <td bgcolor="gray">Dhcp</td>
 *              <td bgcolor="gray">
 *                  您的计算机检测到网络地址为 00A21C2EFEC4 的网卡的 IP 地址 192.168.1.2 已在网络上使用。 计算机会自动获取另一个地址。
 *              </td>
 *          </tr>
 *
 *          <tr>
 *              <td bgcolor="gray">3260</td>
 *              <td bgcolor="gray">信息</td>
 *              <td bgcolor="gray">Workstation</td>
 *              <td bgcolor="gray">
 *                  此计算机成功加入到 workgroup ‘WORKGROUP’。
 *              </td>
 *          </tr>
 *
 *          <tr>
 *              <td bgcolor="gray">4202</td>
 *              <td bgcolor="gray">信息</td>
 *              <td bgcolor="gray">Tcpip</td>
 *              <td bgcolor="gray">
 *                  系统检测到网卡 Realtek…Family PCI Fast Ethernet NIC - 数据包计划程序微型端口 与网络断开， 而且网卡的网络配置已经释放。如果 网卡没有断开，这可能意味着它出现故障。 请与您的供应商联系以获得更新的驱动程序。
 *              </td>
 *          </tr>
 *          <tr>
 *              <td bgcolor="gray">4226</td>
 *              <td bgcolor="gray">警告</td>
 *              <td bgcolor="gray">Tcpip</td>
 *              <td bgcolor="gray">
 *                  TCP/IP 已经达到并发 TCP 连接尝试次数的安全限制。
 *              </td>
 *          </tr>
 *          <tr>
 *              <td bgcolor="gray">4377</td>
 *              <td bgcolor="gray">信息</td>
 *              <td bgcolor="gray">NtServicePack</td>
 *              <td bgcolor="gray">
 *                  Windows XP Hotfix KB873339 was installed.
 *              </td>
 *          </tr>
 *          <tr>
 *              <td bgcolor="gray">6005</td>
 *              <td bgcolor="gray">信息</td>
 *              <td bgcolor="gray">EventLog</td>
 *              <td bgcolor="gray">
 *                  事件日志服务已启动。(开机)
 *              </td>
 *          </tr>
 *          <tr>
 *              <td bgcolor="gray">6006</td>
 *              <td bgcolor="gray">信息</td>
 *              <td bgcolor="gray">EventLog</td>
 *              <td bgcolor="gray">
 *                  事件日志服务已启动。(关机)
 *              </td>
 *          </tr>
 *          <tr>
 *              <td bgcolor="gray">6009</td>
 *              <td bgcolor="gray">信息</td>
 *              <td bgcolor="gray">EventLog</td>
 *              <td bgcolor="gray">
 *                  按ctrl、alt、delete键(非正常)关机
 *              </td>
 *          </tr>
 *          <tr>
 *              <td bgcolor="gray">6011</td>
 *              <td bgcolor="gray">信息</td>
 *              <td bgcolor="gray">EventLog</td>
 *              <td bgcolor="gray">
 *                  此机器的 NetBIOS 名称和 DNS 主机名从 MACHINENAME 更改为 AA。
 *              </td>
 *          </tr>
 *          <tr>
 *              <td bgcolor="gray">7000</td>
 *              <td bgcolor="gray">错误</td>
 *              <td bgcolor="gray">Service Control Manager</td>
 *              <td bgcolor="gray">
 *                  由于下列错误，npkcrypt 服务启动失败:
 *              </td>
 *          </tr>
 *          <tr>
 *              <td bgcolor="gray">7031</td>
 *              <td bgcolor="gray">错误</td>
 *              <td bgcolor="gray">Service Control Manager</td>
 *              <td bgcolor="gray">
 *                  Eset Service 服务意外地终止，这种情况已经出现了 1 次。以下的修正操作将在 0 毫秒内运行: 重新启动服务。
 *              </td>
 *          </tr>
 *          <tr>
 *              <td bgcolor="gray">7035</td>
 *              <td bgcolor="gray">信息</td>
 *              <td bgcolor="gray">Service Control Manager</td>
 *              <td bgcolor="gray">
 *                  xxx服务成功发送一个开始控件。
 *              </td>
 *          </tr>
 *          <tr>
 *              <td bgcolor="gray">7036</td>
 *              <td bgcolor="gray">信息</td>
 *              <td bgcolor="gray">Service Control Manager</td>
 *              <td bgcolor="gray">
 *                  xxx服务处于运行或停止等状态。
 *              </td>
 *          </tr>
 *          <tr>
 *              <td bgcolor="gray">8033</td>
 *              <td bgcolor="gray">信息</td>
 *              <td bgcolor="gray">BROWSER</td>
 *              <td bgcolor="gray">
 *                  由于主浏览器已经停止，浏览器在 \Device\NetBT_Tcpip_{163DE7AB-92AE-499F-8340-B6358A4597CE} 网络上进行强制性的选举。
 *              </td>
 *          </tr>
 *          <tr>
 *              <td bgcolor="gray">10000</td>
 *              <td bgcolor="gray">错误</td>
 *              <td bgcolor="gray">DCOM</td>
 *              <td bgcolor="gray">
 *                  无法启动 DCOM 服务器: {80EE4902-33A8-11D1-A213-0080C88593A5}。 错误:
 *              </td>
 *          </tr>
 *          <tr>
 *              <td bgcolor="gray">15007</td>
 *              <td bgcolor="gray">错误</td>
 *              <td bgcolor="gray">HTTP</td>
 *              <td bgcolor="gray">
 *                  成功地添加了由 URL 前缀 http://*:2869/ 标识的命名空间的保留。
 *              </td>
 *          </tr>
 *          <tr>
 *              <td bgcolor="gray">60054</td>
 *              <td bgcolor="gray">信息</td>
 *              <td bgcolor="gray">Setup</td>
 *              <td bgcolor="gray">
 *                  安装程序成功地完成了安装 Windows 内部版本 2600。
 *              </td>
 *          </tr>
 *          <tr>
 *              <td bgcolor="gray">64002</td>
 *              <td bgcolor="gray">信息</td>
 *              <td bgcolor="gray">Windows File Protection</td>
 *              <td bgcolor="gray">
 *                  试图在被保护的系统文件 c:\windows\system32\quartz.dll 上进行文件替换。 为了维护系统稳定，这个文件被还原成原始版本。 系统文件的文件版本是 6.5.2600.3497。
 *              </td>
 *          </tr>
 *          <tr>
 *              <td bgcolor="gray">64008</td>
 *              <td bgcolor="gray">警告</td>
 *              <td bgcolor="gray">Windows File Protection</td>
 *              <td bgcolor="gray">
 *                  无法验证受保护的 c:\windows\system32\quartz.dll 系统文件，原因是 Windows 文件保护中断。 请过一会儿使用 SFC 工具验证该文件的完整性。
 *              </td>
 *          </tr>
 *     </tbody>
 *</table>
 *
 * @author CH
 */
@Data
public class Event implements Comparable<Event> {
    private int id;
    private Date time;
    private String type;
    private String category;
    private String Source;
    private String log;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Event(int id, Date time, String type, String category, String source, String log) {
        this.id = id;
        this.time = time;
        this.type = type;
        this.category = category;
        Source = source;
        this.log = log;
    }

    @Override
    public int compareTo(Event o) {
        return o.getTime().compareTo(this.getTime());
    }

    @Override
    public String toString() {
        return "Event{" + "id=" + id + ", time=" + simpleDateFormat.format(time) + ", type='" + type + '\'' + ", category='" + category + '\'' + ", Source='" + Source + '\'' + ", log='" + log + '\'' + '}';
    }

}

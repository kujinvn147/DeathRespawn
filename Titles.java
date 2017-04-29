package death.respawn.delay;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Titles
{
  private Class<?> packetTitle;
  private Class<?> packetActions;
  @SuppressWarnings("unused")
  private Class<?> nmsChatSerializer;
  private Class<?> chatBaseComponent;
  private String title = "";
  private ChatColor titleColor = ChatColor.WHITE;
  private String subtitle = "";
  private ChatColor subtitleColor = ChatColor.WHITE;
  private int fadeInTime = -1;
  private int stayTime = -1;
  private int fadeOutTime = -1;
  private boolean ticks = false;
  private static final Map<Class<?>, Class<?>> CORRESPONDING_TYPES = new HashMap<Class<?>, Class<?>>();
  
  public Titles(String title)
  {
    this.title = title;
    loadClasses();
  }
  
  public Titles(String title, String subtitle)
  {
    this.title = title;
    this.subtitle = subtitle;
    loadClasses();
  }
  
  public Titles(Titles title)
  {
    this.title = title.title;
    this.subtitle = title.subtitle;
    this.titleColor = title.titleColor;
    this.subtitleColor = title.subtitleColor;
    this.fadeInTime = title.fadeInTime;
    this.fadeOutTime = title.fadeOutTime;
    this.stayTime = title.stayTime;
    this.ticks = title.ticks;
    loadClasses();
  }
  
  public Titles(String title, String subtitle, int fadeInTime, int stayTime, int fadeOutTime)
  {
    this.title = title;
    this.subtitle = subtitle;
    this.fadeInTime = fadeInTime;
    this.stayTime = stayTime;
    this.fadeOutTime = fadeOutTime;
    loadClasses();
  }
  
  private void loadClasses()
  {
    this.packetTitle = getNMSClass("PacketPlayOutTitle");
    this.packetActions = getNMSClass("PacketPlayOutTitle$EnumTitleAction");
    this.chatBaseComponent = getNMSClass("IChatBaseComponent");
    this.nmsChatSerializer = getNMSClass("IChatBaseComponent$ChatSerializer");
  }
  
  public void setTitle(String title)
  {
    this.title = title;
  }
  
  public String getTitle()
  {
    return this.title;
  }
  
  public void setSubtitle(String subtitle)
  {
    this.subtitle = subtitle;
  }
  
  public String getSubtitle()
  {
    return this.subtitle;
  }
  
  public void setTitleColor(ChatColor color)
  {
    this.titleColor = color;
  }
  
  public void setSubtitleColor(ChatColor color)
  {
    this.subtitleColor = color;
  }
  
  public void setFadeInTime(int time)
  {
    this.fadeInTime = time;
  }
  
  public void setFadeOutTime(int time)
  {
    this.fadeOutTime = time;
  }
  
  public void setStayTime(int time)
  {
    this.stayTime = time;
  }
  
  public void setTimingsToTicks()
  {
    this.ticks = true;
  }
  
  public void setTimingsToSeconds()
  {
    this.ticks = false;
  }
  
  public static void sendTitle(Player player, String title, String subtitle, Integer fadeIn, Integer stay, Integer fadeOut)
  {
    try
    {
      if (title != null)
      {
        title = ChatColor.translateAlternateColorCodes('&', title);
        title = title.replaceAll("%player%", player.getDisplayName());
        
        Object e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
        Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + title + "\"}" });
        Constructor<?> subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] { getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE });
        Object titlePacket = subtitleConstructor.newInstance(new Object[] { e, chatTitle, fadeIn, stay, fadeOut });
        sendPacket(player, titlePacket);
        
        e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
        chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + title + "\"}" });
        subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] { getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent") });
        titlePacket = subtitleConstructor.newInstance(new Object[] { e, chatTitle });
        sendPacket(player, titlePacket);
      }
      if (subtitle != null)
      {
        subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
        subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
        
        Object e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
        Object chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + title + "\"}" });
        Constructor<?> subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] { getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE });
        Object subtitlePacket = subtitleConstructor.newInstance(new Object[] { e, chatSubtitle, fadeIn, stay, fadeOut });
        sendPacket(player, subtitlePacket);
        
        e = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
        chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", new Class[] { String.class }).invoke(null, new Object[] { "{\"text\":\"" + subtitle + "\"}" });
        subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(new Class[] { getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), Integer.TYPE, Integer.TYPE, Integer.TYPE });
        subtitlePacket = subtitleConstructor.newInstance(new Object[] { e, chatSubtitle, fadeIn, stay, fadeOut });
        sendPacket(player, subtitlePacket);
      }
    }
    catch (Exception var11)
    {
      var11.printStackTrace();
    }
  }
  
  public static void sendPacket(Player player, Object packet)
  {
    try
    {
      Object handle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
      Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
      playerConnection.getClass().getMethod("sendPacket", new Class[] { getNMSClass("Packet") }).invoke(playerConnection, new Object[] { packet });
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void clearTitle(Player player)
  {
    try
    {
      Object handle = getHandle(player);
      Object connection = getField(handle.getClass(), "playerConnection")
        .get(handle);
      Object[] actions = this.packetActions.getEnumConstants();
      Method sendPacket = getMethod(connection.getClass(), "sendPacket", new Class[0]);
      Object packet = this.packetTitle.getConstructor(new Class[] { this.packetActions, 
        this.chatBaseComponent }).newInstance(new Object[] {actions[3], null });
      sendPacket.invoke(connection, new Object[] { packet });
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void resetTitle(Player player)
  {
    try
    {
      Object handle = getHandle(player);
      Object connection = getField(handle.getClass(), "playerConnection")
        .get(handle);
      Object[] actions = this.packetActions.getEnumConstants();
      Method sendPacket = getMethod(connection.getClass(), "sendPacket", new Class[0]);
      Object packet = this.packetTitle.getConstructor(new Class[] { this.packetActions, 
        this.chatBaseComponent }).newInstance(new Object[] {actions[4], null });
      sendPacket.invoke(connection, new Object[] { packet });
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  private Class<?> getPrimitiveType(Class<?> clazz)
  {
    return CORRESPONDING_TYPES.containsKey(clazz) ? 
      CORRESPONDING_TYPES.get(clazz) : clazz;
  }
  
  @SuppressWarnings("rawtypes")
private Class<?>[] toPrimitiveTypeArray(Class<?>[] classes)
  {
    int a = classes != null ? classes.length : 0;
	Class[] types = new Class[a];
    for (int i = 0; i < a; i++) {
      types[i] = getPrimitiveType(classes[i]);
    }
    return types;
  }
  
  private static boolean equalsTypeArray(Class<?>[] a, Class<?>[] o)
  {
    if (a.length != o.length) {
      return false;
    }
    for (int i = 0; i < a.length; i++) {
      if ((!a[i].equals(o[i])) && (!a[i].isAssignableFrom(o[i]))) {
        return false;
      }
    }
    return true;
  }
  
  private Object getHandle(Object obj)
  {
    try
    {
      return getMethod("getHandle", obj.getClass(), new Class[0]).invoke(obj, new Object[0]);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  @SuppressWarnings("rawtypes")
private Method getMethod(String name, Class<?> clazz, Class<?>... paramTypes)
  {
    Class[] t = toPrimitiveTypeArray(paramTypes);
    Method[] arrayOfMethod;
    int j = (arrayOfMethod = clazz.getMethods()).length;
    for (int i = 0; i < j; i++)
    {
      Method m = arrayOfMethod[i];
      Class[] types = toPrimitiveTypeArray(m.getParameterTypes());
      if ((m.getName().equals(name)) && (equalsTypeArray(types, t))) {
        return m;
      }
    }
    return null;
  }
  
  private static String getVersion()
  {
    String name = Bukkit.getServer().getClass().getPackage().getName();
    String version = name.substring(name.lastIndexOf('.') + 1) + ".";
    return version;
  }
  
  private static Class<?> getNMSClass(String className)
  {
    String fullName = "net.minecraft.server." + getVersion() + className;
    Class<?> clazz = null;
    try
    {
      clazz = Class.forName(fullName);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return clazz;
  }
  
  private Field getField(Class<?> clazz, String name)
  {
    try
    {
      Field field = clazz.getDeclaredField(name);
      field.setAccessible(true);
      return field;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  private Method getMethod(Class<?> clazz, String name, Class<?>... args)
  {
    Method[] arrayOfMethod;
    int j = (arrayOfMethod = clazz.getMethods()).length;
    for (int i = 0; i < j; i++)
    {
      Method m = arrayOfMethod[i];
      if ((m.getName().equals(name)) && (
        (args.length == 0) || 
        (ClassListEqual(args, m.getParameterTypes()))))
      {
        m.setAccessible(true);
        return m;
      }
    }
    return null;
  }
  
  private boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2)
  {
    boolean equal = true;
    if (l1.length != l2.length) {
      return false;
    }
    for (int i = 0; i < l1.length; i++) {
      if (l1[i] != l2[i])
      {
        equal = false;
        break;
      }
    }
    return equal;
  }
}

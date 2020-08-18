import discord
import asyncio
import time
from random import *
from math import *


client = discord.Client()

@client.event
async def on_ready():
    print("Online")
    while client.is_ready():
        await client.change_presence(activity=discord.Game("Yatopia.jar"))
        await asyncio.sleep(15)
        await client.change_presence(activity=discord.Activity(type=discord.ActivityType.watching, name="over you ♥"))
        await asyncio.sleep(15)
        

@client.event
async def on_message(msg):
    if msg.content.lower().startswith("?"):		
        cmd = msg.content.lower().replace("?", "")
        if cmd == ("jdk15"):
            await msg.channel.send("JDK 15 is the fastest JDK that works with Yatopia, you can download it here: https://jdk.java.net/15/ . Run `?flags` for flag information.")
            await msg.channel.send("WARNING: JDK 15 is currently in the a pre-release state meaning it may contain issues, use it at your own risk.")
        elif cmd == ("openj91.16.2"):
            await msg.channel.send("You can't start Minecraft 1.16.2 with OpenJ9 and we do not recommend OpenJ9, use `?flags` for the recommended flags.")
        elif cmd == ("jdk14"):
            await msg.channel.send("JDK 14 is one of the fastest stable JDKs so it's nice for a production environment")
            await msg.channel.send("Download from here: https://jdk.java.net/14/")
        elif cmd == ("flags"):
            await msg.channel.send("ZGC is thes best garbage collector for Yatopia.")
            await msg.channel.send("<https://frama.link/Yatopiazgcflags> are the recommended flags to use.")
        elif cmd == ("wiki"):
            await msg.channel.send("<https://github.com/YatopiaMC/Yatopia/wiki>")
        elif cmd == ("1.15.2"):
            await msg.channel.send("<https://github.com/YatopiaMC/Yatopia/actions?query=branch%3Aver%2F1.15.2>")
        elif cmd == ("1.16.1"):
            await msg.channel.send("<https://github.com/YatopiaMC/Yatopia/actions?query=branch%3Aver%2F1.16.1>")
        elif cmd == ("1.16.2"):
            await msg.channel.send("<https://github.com/YatopiaMC/Yatopia/actions?query=branch%3Aver%2F1.16.2>")
        elif cmd == ("download"):
            await msg.channel.send("Run `?1.15.2` or `?1.16.1` or `?1.16.2`")
        elif cmd == ("invite"):
            await msg.channel.send("https://discord.io/YatopiaMC")
        elif cmd == ("openj9"):
            await msg.channel.send("OpenJ9 doesn't run correctly with Yatopia, please use ZGC and the recommended flags (run `?flags`)")
        elif cmd == ("website"):
            await msg.channel.send("https://yatopia.net/")
        elif cmd == ("ask"):
            e=discord.Embed()
            e.set_image(url="https://media.discordapp.net/attachments/613163671870242842/674294268646391828/93qXFd0-2.png")
            await msg.channel.send(embed=e)
        elif cmd == ("help"):
            embed=discord.Embed(title="Help", description="List of all commands", color=0xffff00)
            embed.set_thumbnail(url="https://cdn.discordapp.com/attachments/745296670631395482/745298764788400238/yatipia.png")
            embed.add_field(name="?download", value="Download information", inline=True)
            embed.add_field(name="?jdk14", value="JDK 14 information", inline=True)
            embed.add_field(name="?jdk15", value="JDK 15 information", inline=True)
            embed.add_field(name="?flags", value="Recommended flags", inline=True)
            embed.add_field(name="?openj9", value="‫OpenJ9 information", inline=True)
            embed.add_field(name="?openj91.16.2", value="‫OpenJ9 specific information for 1.16.2", inline=True)
            embed.add_field(name="?wiki", value="Link to the wiki", inline=True)
            embed.add_field(name="?invite", value="Invite link", inline=True)
            embed.add_field(name="?website", value="Link to the website", inline=True)
            embed.add_field(name="?ask", value="Whenever someone is asking if he can ask", inline=True)
            await msg.channel.send(embed=embed)




            
client.run("")

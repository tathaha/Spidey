package me.canelex.Spidey;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang3.StringUtils;

import me.canelex.Spidey.api.API;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.audit.ActionType;
import net.dv8tion.jda.core.audit.AuditLogEntry;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Guild.Ban;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildController;

public class Events extends ListenerAdapter {
	
	Calendar cal = Calendar.getInstance();	
	Locale locale = new Locale("en", "EN");    	
	SimpleDateFormat date = new SimpleDateFormat("EEEE, d.LLLL Y", locale);      
	SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", locale);	
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("groovy");  
	
	@Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
    	
    	Message msg = e.getMessage();
    	TextChannel msgCh = e.getChannel();
        
        if (msg.getContentRaw().equalsIgnoreCase("s!info")) {   
            
        	User dev = e.getJDA().asBot().getApplicationInfo().complete().getOwner();
    		EmbedBuilder eb = new EmbedBuilder();
    		eb.setAuthor("About bot", "https://canelex.ymastersk.net", e.getJDA().getSelfUser().getAvatarUrl());
    		eb.setColor(Color.WHITE);
    		eb.addField("Developer", dev.getAsMention(), true);
    		eb.addField("Release channel", "**STABLE**", true);
    		eb.setThumbnail(e.getGuild().getIconUrl());
    		eb.setFooter("Command executed by " + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator(), e.getAuthor().getAvatarUrl());    		
    		API.sendMessage(msgCh, eb.build());        		   		
      	   	    		
    	}               
        
        if (msg.getContentRaw().equalsIgnoreCase("s!membercount")) {
        	
        	int total = e.getGuild().getMembers().size();        	
        	long online = e.getGuild().getMembers().stream().filter(member -> member.getOnlineStatus() == OnlineStatus.ONLINE || member.getOnlineStatus() == OnlineStatus.IDLE || member.getOnlineStatus() == OnlineStatus.DO_NOT_DISTURB).count();
        	long bots = e.getGuild().getMembers().stream().filter(member -> member.getUser().isBot()).count();
        	online = online - bots;
        	
        	EmbedBuilder eb = new EmbedBuilder();
        	eb.setTitle("MEMBERCOUNT");
        	eb.setColor(Color.WHITE);
        	eb.addField("People", "**" + (total - bots) + "**", true);        	
        	eb.addField("People", "**" + (total - bots) + "**", true);
        	eb.addField("Bots", "**" + bots + "**", true);
        	eb.addField("Online", "**" + online + "**", true);
        	//TODO metoda v API na auto-footer
    		eb.setFooter("Command executed by " + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator(), e.getAuthor().getAvatarUrl());        	
           	API.sendMessage(msgCh, eb.build());
           	
        }
        
        if (msg.getContentRaw().startsWith("s!joindate")) {
        	    	    					   	
    		String joindate = date.format(cal.getTime()).toString();
    		String jointime = time.format(cal.getTime()).toString();    		
        	
        	if (msg.getMentionedUsers().isEmpty()) {
        		
        		Member member = API.getMember(e.getGuild(), e.getAuthor());
        		cal.setTimeInMillis(member.getJoinDate().toInstant().toEpochMilli());        		
        		API.sendPrivateMessage(e.getAuthor(), "Date and time of joining to this guild: **" + joindate + "** | **" + jointime + "**!");
        		
        	}
        	
        	else {
        		
        		List<User> mentioned = msg.getMentionedUsers();
        		
        		for (User user : mentioned) {
        			
        			Member member = API.getMember(e.getGuild(), user);
            		cal.setTimeInMillis(member.getJoinDate().toInstant().toEpochMilli());
            		API.sendPrivateMessage(e.getAuthor(), "(**" + member.getEffectiveName() + "**) " + "Date and time of joining to this guild: **" + joindate + "** | **" + jointime + "**!");          		
        		
        		}
        		
        	}
        	
        }       
        
        if (msg.getContentRaw().equalsIgnoreCase("s!server")) {
        	
        	EmbedBuilder eb = new EmbedBuilder();
        	eb.setTitle(e.getGuild().getName());
        	eb.setColor(Color.ORANGE);
        	eb.setThumbnail(e.getGuild().getIconUrl());
        	    		
    		eb.addField("Owner", "**" + e.getGuild().getOwner().getAsMention() + "**", true);
    		
        	cal.setTimeInMillis(e.getGuild().getCreationTime().toInstant().toEpochMilli());
    		String creatdate = date.format(cal.getTime()).toString();   
    		String creattime = time.format(cal.getTime()).toString();   
        	eb.addField("Created", "**" + creatdate + "** | **" + creattime + "**", true);
        	
    		cal.setTimeInMillis(API.getMember(e.getGuild(), e.getJDA().getSelfUser()).getJoinDate().toInstant().toEpochMilli());
    		String joindate = date.format(cal.getTime()).toString();   
    		String jointime = time.format(cal.getTime()).toString();    		
        	eb.addField("Bot connected", "**" + joindate + "** | ** " + jointime + "**", true);
        	
    		eb.setFooter("Command executed by " + e.getAuthor().getName() + "#" + e.getAuthor().getDiscriminator(), e.getAuthor().getAvatarUrl());        	
			API.sendMessage(msgCh, eb.build());
        
        }               
    	
    	if (msg.getContentRaw().equalsIgnoreCase("s!log")) {
    		
    		if (API.hasPerm(e.getGuild(), e.getAuthor(), Permission.ADMINISTRATOR)) {
    			
        		MySQL.createGuildTable(e.getGuild().getId());
        		MySQL.saveLogChannel(e.getGuild().getId(), e.getTextChannel().getId());
        		API.sendMessage(msgCh, ":white_check_mark: Log channel set to " + e.getTextChannel().getAsMention());
    			
    		}
    		
    		else {
    			
        		API.sendMessage(msgCh, ":no_entry: Action can't be completed due to missing permission **ADMINISTRATOR**.");    			
    			
    		}
    		    		
    	}
    	
    	if (msg.getContentRaw().startsWith("s!mute")) {  
    		
			Guild guild = e.getGuild();    		
			TextChannel c = guild.getTextChannelById(MySQL.getLogChannel(guild.getId()));    		
    		
    		List<User> men = msg.getMentionedUsers();
    		
    		if (!msg.getContentRaw().equals("s!mute")) {
    			
    			if (e.getGuild().getMember(e.getAuthor()).hasPermission(Permission.BAN_MEMBERS)) {
    				
            		String length = msg.getContentRaw().substring(5 + 2);
            		length = length.substring(0, length.indexOf(" "));
            		
            		String time = msg.getContentRaw().substring((6 + 2 + length.length()));
            		time = time.substring(0, time.indexOf(" "));
            		
            		String reason = msg.getContentRaw().substring((7 + 2 + length.length() + time.length()));
            		reason = reason.substring(0, reason.lastIndexOf(" "));
            		
            		int lengthV = Integer.valueOf(length);
            		
            		if (!men.isEmpty()) {
            			
            			for (User user : men) {
            				
            				Role muted = e.getGuild().getRolesByName("Muted", true).get(0);
            				Member member = e.getGuild().getMember(user);
            				GuildController controller = guild.getController();
            				
            	    		API.deleteMessage(msg);        				
            				controller.addSingleRoleToMember(member, muted).queue();
            				
            				if (StringUtils.isNumeric(length) && lengthV != 0) {
            					
                				if (time.equalsIgnoreCase("d")) {
                					
                    				EmbedBuilder eb = new EmbedBuilder();
                    				eb.setTitle("NEW MUTE");
                    				eb.setThumbnail(user.getAvatarUrl());
                    				eb.setColor(Color.RED);
                    				eb.addField("User", user.getAsMention(), true);
                    				eb.addField("Moderator", e.getAuthor().getAsMention(), true);
                    				eb.addField("Reason", "**" + reason + "**", true);              				
                    				
                    				if (lengthV == 1) {
                    					
                    					eb.addField("Length", "**1** day", true);            					
                    					
                    				}
                    				
                    				else {
                    					
                    					eb.addField("Length", "**" + lengthV + "** days", true);                   					
                    					
                    				}
                    				
                    				API.sendMessage(c, eb.build());           					
                					
                    				controller.removeSingleRoleFromMember(member, muted).queueAfter(lengthV, TimeUnit.DAYS);  
                					
                				}
                				
                				else {
                					
                    				if (time.equalsIgnoreCase("w")) {
                    					
                        				EmbedBuilder eb = new EmbedBuilder();
                        				eb.setTitle("NEW MUTE");
                        				
                        		        if (user.getAvatarUrl() == null) {
                        		        	
                        		        	eb.setThumbnail(user.getDefaultAvatarUrl());
                        		        	
                        		        }
                        		        
                        		        else {
                        		        	
                        		            eb.setThumbnail(user.getAvatarUrl());        	
                        		        	
                        		        }
                        		        
                        				eb.setColor(Color.RED);
                        				eb.addField("User", user.getAsMention(), true);
                        				eb.addField("Moderator", e.getAuthor().getAsMention(), true);
                        				eb.addField("Reason", "**" + reason + "**", true);           				
                        				
                        				if (lengthV == 1) {
                        					
                        					eb.addField("Length", "**1** week", true);                      					
                        					
                        				}
                        				
                        				else {
                        					
                        					eb.addField("Length", "**" + lengthV + "** weeks", true);                        					
                        					
                        				}                
                        				
                        				API.sendMessage(c, eb.build());                   				
                    					
                        				controller.removeSingleRoleFromMember(member, muted).queueAfter((lengthV * 7), TimeUnit.DAYS);                     				
                    					
                    				}
                    				
                    				else {
                    					
                        				if (time.equalsIgnoreCase("m")) {
                        					
                            				EmbedBuilder eb = new EmbedBuilder();
                            				eb.setTitle("NEW MUTE");
                            				
                            		        if (user.getAvatarUrl() == null) {
                            		        	
                            		        	eb.setThumbnail(user.getDefaultAvatarUrl());
                            		        	
                            		        }
                            		        
                            		        else {
                            		        	
                            		            eb.setThumbnail(user.getAvatarUrl());        	
                            		        	
                            		        }
                            		        
                            				eb.setColor(Color.RED);
                            				eb.addField("User", user.getAsMention(), true);
                            				eb.addField("Moderator", e.getAuthor().getAsMention(), true);
                            				eb.addField("Reason", "**" + reason + "**", true);                              				
                            				
                            				if (lengthV == 1) {
                            					
                            					eb.addField("Length", "**1** minute", true);                           					
                                				API.sendMessage(c, eb.build());                              					                              				
                            					
                            				}
                            				
                            				else {                            					
                            						
                                				eb.addField("Length", "**" + lengthV + "** minut", true);                        						                                					
                            					
                                				API.sendMessage(c, eb.build());                    					                                  				
                            					
                            				}                    					                    					
                            				
                            				controller.removeSingleRoleFromMember(member, muted).queueAfter(lengthV, TimeUnit.MINUTES);                             				                        				
                        					
                        				}
                        				
                        				else {
                        					
                        					API.sendMessage(msgCh, "Unknown time value. Value must be **d**ay(s), **w**eek(s) or **m**inute(s).");
                        					
                        				}
                    					
                    				}        				    					
                					
                				}        					
            					
            				}
            				
            				else {
            					
            					API.sendMessage(msgCh, "Given value is not a number.");         					
            					
            				}        				       				
            				
            			}
            			
            		}
            		
            		else {
            			
        				API.sendMessage(msgCh, "Unknown arguments..");        			
            			
            		}     				
    				
    			}
    			
    			else {
    				
    				API.sendMessage(msgCh, ":no_entry: Action can't be completed due to missing permission **BAN_MEMBERS**.");      				
    				
    			}
    			
    		} 
    		
    		else {
    			
				API.sendMessage(msgCh, "Unknown arguments..");    			
    			
    		}   	
		    	
    	}
    	
    	if (msg.getContentRaw().startsWith("s!warn")) {
    		
    		if (!msg.getContentRaw().equals("s!warn")) {
    			
    			if (e.getMember().hasPermission(Permission.BAN_MEMBERS)) {
    				
    				final String reason;
    				reason = msg.getContentRaw().substring(7, msg.getContentRaw().lastIndexOf(" "));
    				
    				for (User u : msg.getMentionedUsers()) {
    				
    				u.openPrivateChannel().queue(ch -> { 
    					
    					ch.sendMessage(":exclamation: You just got a warn on server **" + e.getGuild().getName() + "** from **" + e.getAuthor().getName() + "**. Reason - **" + reason + "**.").queue();
    					API.deleteMessage(msg);
    					EmbedBuilder eb = new EmbedBuilder();
    					eb.setTitle("NEW WARN");
    					eb.setColor(Color.ORANGE);
    					eb.addField("User", u.getAsMention(), true);
    					eb.addField("Moderator", e.getAuthor().getAsMention(), true);
    					eb.addField("Reason", "**" + reason + "**", true);
    					TextChannel log = e.getGuild().getTextChannelById(MySQL.getLogChannel(e.getGuild().getId()));
    					API.sendMessage(log, eb.build());    
    					
    				});
    				
    			}}
    			
    			else {
    				
    				API.sendMessage(msgCh, ":no_entry: Action can't be completed due to missing permission **BAN_MEMBERS**.");    				
    				
    			}
    			
    		}
    	}
    	
    	if (msg.getContentRaw().startsWith("s!ban")) {
    		
    		if (!msg.getContentRaw().equals("s!ban")) {
    			
    			if (e.getMember().hasPermission(Permission.BAN_MEMBERS)) {
    				
            		String id = msg.getContentRaw().substring(6);
            		id = id.substring(0, id.indexOf(" "));
            		
            		String reason = msg.getContentRaw().substring((7 + id.length()));
            		
    				e.getGuild().getController().ban(id, 0, reason).queue();   				    				
    				
    			}
    			
    			else {
    				
    				API.sendMessage(msgCh, ":no_entry: Action can't be completed due to missing permission **BAN_MEMBERS**.");    				
    				
    			}    			
    			
    		}
    		
    	}    	
        
	}
	
	@Override
	public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent e) {
		
		Role muted = e.getGuild().getRolesByName("Muted", true).get(0);
		
		if (e.getRoles().contains(muted)) {
			
			TextChannel log = e.getGuild().getTextChannelById(MySQL.getLogChannel(e.getGuild().getId()));
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("UNMUTE");
			eb.setColor(Color.GREEN);
			
	        if (e.getUser().getAvatarUrl() == null) {
	        	
	        	eb.setThumbnail(e.getUser().getDefaultAvatarUrl());
	        	
	        }
	        
	        else {
	        	
	            eb.setThumbnail(e.getUser().getAvatarUrl());        	
	        	
	        }			
			
			eb.addField("User", "**" + e.getUser().getName() + "**", false);
			API.sendMessage(log, eb.build());
			
		}
		
	}
	
	@Override
    public void onGuildBan(GuildBanEvent e) {
		
		User user = e.getUser();
		TextChannel log = e.getGuild().getTextChannelById(MySQL.getLogChannel(e.getGuild().getId()));	
        
        Ban ban = e.getGuild().getBan(user).complete();
        List<AuditLogEntry> auditbans = e.getGuild().getAuditLogs().type(ActionType.BAN).complete();
        User banner = auditbans.get(0).getUser();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("NEW BAN");
        
        if (user.getAvatarUrl() == null) {
        	
        	eb.setThumbnail(user.getDefaultAvatarUrl());
        	
        }
        
        else {
        	
            eb.setThumbnail(user.getAvatarUrl());        	
        	
        }        
        
        eb.setColor(Color.RED);
        eb.addField("User", "**" + user.getName() + "**", true);
        eb.addField("ID", "**" + user.getId() + "**", true);        
        eb.addField("Moderator", banner.getAsMention(), true);
        eb.addField("Reason", "**" + ban.getReason() + "**", true);        
		API.sendMessage(log, eb.build());
		
	}	
	
	@Override
    public void onGuildUnban(GuildUnbanEvent e) {
		
		User user = e.getUser();
		TextChannel log = e.getGuild().getTextChannelById(MySQL.getLogChannel(e.getGuild().getId()));	
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("UNBAN");
        eb.setColor(Color.GREEN);
        
        if (user.getAvatarUrl() == null) {
        	
        	eb.setThumbnail(user.getDefaultAvatarUrl());
        	
        }
        
        else {
        	
            eb.setThumbnail(user.getAvatarUrl());        	
        	
        }        
               
        eb.addField("User", "**" + user.getName() + "**", true);
        eb.addField("ID", "**" + user.getId() + "**", true);        
		API.sendMessage(log, eb.build());
		
	}	
	
	@Override
	public void onGuildJoin(GuildJoinEvent e) {
		
		GuildController gc = e.getGuild().getController();
		gc.createRole().setName("Muted").setColor(Color.GRAY).queue(r -> {
			
			r.getManager().revokePermissions(EnumSet.of(Permission.MESSAGE_WRITE)).queue();
			
		});
		
	}
	
	@Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent e) {
		
		User user = e.getUser();
        TextChannel channel = e.getGuild().getTextChannelById(MySQL.getLogChannel(e.getGuild().getId()));	
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("USER HAS LEFT");
        
        if (user.getAvatarUrl() == null) {
        	
        	eb.setThumbnail(user.getDefaultAvatarUrl());
        	
        }
        
        else {
        	
            eb.setThumbnail(user.getAvatarUrl());        	
        	
        }        
        
        eb.setColor(Color.RED);
        eb.addField("User", "**" + user.getName() + "**", true);
        eb.addField("ID", "**" + user.getId() + "**", true);
        API.sendMessage(channel, eb.build());        	
		
	}	
	
	@Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		
		User user = e.getUser();
        TextChannel channel = e.getGuild().getTextChannelById(MySQL.getLogChannel(e.getGuild().getId()));	
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("USER HAS JOINED");
        
        if (user.getAvatarUrl() == null) {
        	
        	eb.setThumbnail(user.getDefaultAvatarUrl());
        	
        }
        
        else {
        	
            eb.setThumbnail(user.getAvatarUrl());        	
        	
        }        
              
        eb.setColor(Color.GREEN);
        eb.addField("User", "**" + user.getName() + "**", true);
        eb.addField("ID", "**" + user.getId() + "**", true);        
        API.sendMessage(channel, eb.build());    
		
	}	
		
}

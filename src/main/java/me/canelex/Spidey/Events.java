package me.canelex.Spidey;

import java.awt.Color;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;

import me.canelex.Spidey.utils.API;
import me.canelex.Spidey.utils.Emoji;
import me.canelex.Spidey.utils.PermissionError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Guild.Ban;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.GuildController;

public class Events extends ListenerAdapter {
		
	Locale locale = new Locale("en", "EN");  
	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));        	
	SimpleDateFormat date = new SimpleDateFormat("EEEE, d.LLLL Y", locale);      
	SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss", locale); 	
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("groovy");  
	
	@Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
    	
    	final Message msg = e.getMessage();
    	final TextChannel msgCh = e.getChannel();
    	final Member mem = e.getMember();
    	final User author = e.getAuthor();
    	final Guild guild = e.getGuild();
    	final JDA jda = e.getJDA();
        
        if (msg.getContentRaw().equalsIgnoreCase("s!info")) {   
            
        	User dev = jda.retrieveApplicationInfo().complete().getOwner();
    		EmbedBuilder eb = API.createEmbedBuilder(author);
    		eb.setAuthor("About bot", "https://canelex.ymastersk.net", jda.getSelfUser().getAvatarUrl());
    		eb.setColor(Color.WHITE);
    		eb.addField("Developer", dev.getAsMention(), true);
    		eb.addField("Release channel", "**STABLE**", true);
    		eb.setThumbnail(guild.getIconUrl());		
    		API.sendMessage(msgCh, eb.build());        		   		
      	   	    		
    	}               
        
        if (msg.getContentRaw().equalsIgnoreCase("s!membercount")) {
        	
        	List<Member> tonline = guild.getMembers().stream().filter(member -> member.getOnlineStatus() == OnlineStatus.ONLINE || member.getOnlineStatus() == OnlineStatus.IDLE || member.getOnlineStatus() == OnlineStatus.DO_NOT_DISTURB).collect(Collectors.toList());
        	long bonline = tonline.stream().filter(m -> m.getUser().isBot()).count();        	
        	long total = guild.getMembers().size();        	
        	long online = tonline.size();
        	long bots = guild.getMembers().stream().filter(member -> member.getUser().isBot()).count();
        	long ponline = online - bonline;
        	
        	EmbedBuilder eb = API.createEmbedBuilder(author);
        	eb.setTitle("MEMBERCOUNT");
        	eb.setColor(Color.WHITE);
        	eb.addField("Total", "**" + total + "**", true);        	
        	eb.addField("People", "**" + (total - bots) + "**", true);
        	eb.addField("Bots", "**" + bots + "**", true);
        	eb.addField("Total online", "**" + online + "**", true);
        	eb.addField("People online", "**" + ponline + "**", true);
        	eb.addField("Bots online", "**" + bonline + "**", true);        	
           	API.sendMessage(msgCh, eb.build());
           	
        }
        
        if (msg.getContentRaw().startsWith("s!joindate")) {        	    	    					   	       	    	
        	
        	if (msg.getMentionedUsers().isEmpty()) {
        		
        		cal.setTimeInMillis(mem.getTimeJoined().toInstant().toEpochMilli()); 
        		String joindate = date.format(cal.getTime()).toString();
        		String jointime = time.format(cal.getTime()).toString();        		
        		API.sendPrivateMessage(author, "Date and time of joining to guild **" + guild.getName() + "**: **" + joindate + "** | **" + jointime + "** UTC", false);
        		
        	}
        	
        	else {
        		
        		List<User> mentioned = msg.getMentionedUsers();
        		
        		for (User user : mentioned) {
        			
        			Member member = API.getMember(guild, user);
            		cal.setTimeInMillis(member.getTimeJoined().toInstant().toEpochMilli());
            		String joindate = date.format(cal.getTime()).toString();
            		String jointime = time.format(cal.getTime()).toString();            		
            		API.sendPrivateMessage(author, "(**" + member.getEffectiveName() + "**) " + "Date and time of joining to guild **" + guild.getName() + "**: **" + joindate + "** | **" + jointime + "** UTC", false);          		
        		
        		}
        		
        	}
        	
        }       
        
        if (msg.getContentRaw().equalsIgnoreCase("s!server")) {
        	
        	EmbedBuilder eb = API.createEmbedBuilder(author);         	
        	eb.setTitle(guild.getName());
        	eb.setColor(Color.ORANGE);
        	eb.setThumbnail(guild.getIconUrl());
        	eb.setDescription("Server ID: **" + guild.getId() + "**");
        	    		
    		eb.addField("Owner", "**" + guild.getOwner().getAsMention() + "**", false);
    		
        	cal.setTimeInMillis(guild.getTimeCreated().toInstant().toEpochMilli());
    		String creatdate = date.format(cal.getTime()).toString();   
    		String creattime = time.format(cal.getTime()).toString();   
        	eb.addField("Created", "**" + creatdate + "** | **" + creattime + "** UTC", false);
        	
    		cal.setTimeInMillis(API.getMember(guild, jda.getSelfUser()).getTimeJoined().toInstant().toEpochMilli());
    		String joindate = date.format(cal.getTime()).toString();   
    		String jointime = time.format(cal.getTime()).toString();    		
        	eb.addField("Bot connected", "**" + joindate + "** | ** " + jointime + "** UTC", false);
        	
	        eb.addField("Custom invite URL", (!API.isPartnered(guild) ? "Guild is not partnered" : guild.retrieveVanityUrl().complete()), false);
        	
        	String s = ""; //by @maasterkoo
        	
            int i = 0;
            
            List<Role> roles = guild.getRoles().stream().collect(Collectors.toCollection(ArrayList::new));
            roles.remove(guild.getPublicRole());
            
            for (Role role : roles) {
            	
                i++;
                
                if (i == roles.size()) {
                	
                    s += role.getName();
                    
                }    
                
                else {
                	
                    s += role.getName() + ", ";
                 
                }    
                
            }
            
        	eb.addField("Roles [**" + i + "**]", s, false);
        	       	
			API.sendMessage(msgCh, eb.build());
        
        }               
    	
    	if (msg.getContentRaw().equalsIgnoreCase("s!log")) {
    		
    		final String neededPerm = "ADMINISTRATOR";
    		
    		if (API.hasPerm(mem, Permission.valueOf(neededPerm))) {
    			
    			if (guild.getSystemChannel() != null) {
    				
    				guild.getManager().setSystemChannel(null).queue();    				
    				
    			}
    			
    			if (!MySQL.isInDatabase(guild.getIdLong())) {    			
        				    				
                	MySQL.insertData(guild.getIdLong(), msgCh.getIdLong());
                	API.sendMessage(msgCh, ":white_check_mark: Log channel set to " + msgCh.getAsMention() + ". Type this command again to set log channel to default guild channel.", false);    				    				
    				
    			}
    			
    			else {
    				
        			if (MySQL.getChannelId(guild.getIdLong()).equals(msgCh.getIdLong())) {
        				
        				MySQL.removeData(guild.getIdLong());
        				MySQL.insertData(guild.getIdLong(), guild.getDefaultChannel().getIdLong());
        				API.sendMessage(msgCh, ":white_check_mark: Log channel set to " + guild.getDefaultChannel().getAsMention() + ". Type this command again in channel you want to be as log channel.", false);
        				
        			}
        			
        			else {
        				
        				MySQL.removeData(guild.getIdLong());    				
                		MySQL.insertData(guild.getIdLong(), msgCh.getIdLong());
                		API.sendMessage(msgCh, ":white_check_mark: Log channel set to " + msgCh.getAsMention() + ". Type this command again to set log channel to default guild channel.", false);    				
        				
        			}        			
    				
    			}

    		}
    		
    		else {
    			
        		API.sendMessage(msgCh, PermissionError.getErrorMessage(neededPerm), false);    			
    			
    		}
    		    		
    	}
    	
    	if (msg.getContentRaw().startsWith("s!mute")) {  
    		   		
			GuildController controller = guild.getController();			
			TextChannel c = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));
    		final String neededPerm = "BAN_MEMBERS";			
    		
    		List<User> men = msg.getMentionedUsers();
    		
    		if (!msg.getContentRaw().equals("s!mute")) {
    			
    			if (API.hasPerm(mem, Permission.valueOf(neededPerm))) {
    				
    				if (guild.getRolesByName("Muted", false).isEmpty()) {
    					
    					controller.createRole().setName("Muted").setColor(Color.GRAY).complete();    					
    					
    				}
    				
            		String length = msg.getContentRaw().substring(5 + 2);
            		length = length.substring(0, length.indexOf(" "));
            		
            		String time = msg.getContentRaw().substring((6 + 2 + length.length()));
            		time = time.substring(0, time.indexOf(" "));
            		
            		String reason = msg.getContentRaw().substring((7 + 2 + length.length() + time.length()));
            		reason = reason.substring(0, reason.lastIndexOf(" "));
            		
            		int lengthV = Integer.valueOf(length);
            		
            		if (!men.isEmpty()) {
            			
            			for (User user : men) {
            				
            				Role muted = guild.getRolesByName("Muted", false).get(0);
            				Member member = guild.getMember(user);
            				
            	    		API.deleteMessage(msg);        				
            				controller.addSingleRoleToMember(member, muted).queue();
            				
            				if (StringUtils.isNumeric(length) && lengthV != 0) {
            					
                				if (time.equalsIgnoreCase("d")) {
                					
                    				EmbedBuilder eb = API.createEmbedBuilder(author);
                    				eb.setTitle("NEW MUTE");                    				
                    		        eb.setThumbnail((user.getAvatarUrl() == null ? user.getDefaultAvatarUrl() : user.getAvatarUrl()));                    				                    				
                    				eb.setColor(Color.RED);
                    				eb.addField("User", user.getAsMention(), true);
                    				eb.addField("Moderator", author.getAsMention(), true);
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
                    					
                        				EmbedBuilder eb = API.createEmbedBuilder(author);
                        				eb.setTitle("NEW MUTE");                        				
                        		        eb.setThumbnail((user.getAvatarUrl() == null ? user.getDefaultAvatarUrl() : user.getAvatarUrl()));                        		        
                        				eb.setColor(Color.RED);
                        				eb.addField("User", user.getAsMention(), true);
                        				eb.addField("Moderator", author.getAsMention(), true);
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
                        					
                            				EmbedBuilder eb = API.createEmbedBuilder(author);
                            				eb.setTitle("NEW MUTE");                            				
                            		        eb.setThumbnail((user.getAvatarUrl() == null ? user.getDefaultAvatarUrl() : user.getAvatarUrl()));                            		        
                            				eb.setColor(Color.RED);
                            				eb.addField("User", user.getAsMention(), true);
                            				eb.addField("Moderator", author.getAsMention(), true);
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
                        					
                        					API.sendMessage(msgCh, "Unknown time value. Value must be **d**ay(s), **w**eek(s) or **m**inute(s).", false);
                        					
                        				}
                    					
                    				}        				    					
                					
                				}        					
            					
            				}
            				
            				else {
            					
            					API.sendMessage(msgCh, "Given value is not a number.", false);         					
            					
            				}        				       				
            				
            			}
            			
            		}
            		
            		else {
            			
        				API.sendMessage(msgCh, "Unknown arguments..", false);        			
            			
            		}     				
    				
    			}
    			
    			else {
    				
    				API.sendMessage(msgCh, PermissionError.getErrorMessage(neededPerm), false);      				
    				
    			}
    			
    		} 
    		
    		else {
    			
				API.sendMessage(msgCh, "Unknown arguments..", false);    			
    			
    		}   	
		    	
    	}
    	
    	if (msg.getContentRaw().startsWith("s!warn")) {
    		
    		final String neededPerm = "BAN_MEMBERS";    		
    		
    		if (!msg.getContentRaw().equals("s!warn")) {
    			
    			if (API.hasPerm(mem, Permission.valueOf(neededPerm))) {
    				
    				final String reason;
    				reason = msg.getContentRaw().substring(7, msg.getContentRaw().lastIndexOf(" "));
    				
    				for (User u : msg.getMentionedUsers()) {
    				
    				  u.openPrivateChannel().queue(ch -> ch.sendMessage(":exclamation: You have been warned on guild **" + guild.getName() + "** from **" + author.getName() + "**. Reason - **" + reason + "**.").queue());
    				
					  API.deleteMessage(msg);
					  EmbedBuilder eb = API.createEmbedBuilder(author);
					  eb.setTitle("NEW WARN");
					  eb.setColor(Color.ORANGE);
					  eb.addField("User", u.getAsMention(), true);
					  eb.addField("Moderator", author.getAsMention(), true);
					  eb.addField("Reason", "**" + reason + "**", true);
					  TextChannel log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));
					  API.sendMessage(log, eb.build());    				
    				
    			  }
    				
    			}
    			
    			else {
    				
    				API.sendMessage(msgCh, PermissionError.getErrorMessage(neededPerm), false);    				
    				
    			}
    			
    		}
    	}
    	
    	if (msg.getContentRaw().startsWith("s!ban")) {
    		
    		final String neededPerm = "BAN_MEMBERS";    		
    		
    		if (!msg.getContentRaw().equals("s!ban")) {
    			
    			if (mem.hasPermission(Permission.valueOf(neededPerm))) {
    				
            		String id = msg.getContentRaw().substring(6);
            		id = id.substring(0, id.indexOf(" "));
            		
            		String reason = msg.getContentRaw().substring((7 + id.length()));
            		
    				guild.getController().ban(id, 0, reason).queue();   				    				
    				
    			}
    			
    			else {
    				
    				API.sendMessage(msgCh, PermissionError.getErrorMessage(neededPerm), false);    				
    				
    			}    			
    			
    		}
    		
    	} 
    	
    	if (msg.getContentRaw().startsWith("s!poll")) {
    		
    		final String neededPerm = "BAN_MEMBERS";    		
    		
    		TextChannel log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));	   		
    		
    		if (!API.hasPerm(mem, Permission.valueOf(neededPerm))) {
    			
				API.sendMessage(msgCh, PermissionError.getErrorMessage(neededPerm), false);      			
    			
    		}
    		
    		else {
    			
        		String question = msg.getContentRaw().substring(7);
        		API.deleteMessage(msg);
        		e.getChannel().sendMessage("Poll: **" + question + "**").queue(m -> {
        			
        			m.addReaction(Emoji.like).queue();
        			m.addReaction(Emoji.shrug).queue();
        			m.addReaction(Emoji.dislike).queue();
            		EmbedBuilder eb = API.createEmbedBuilder(author);
            		eb.setTitle("NEW POLL");
            		eb.setColor(Color.ORANGE);             		
            		eb.addField("Question", "**" + question + "**", false);
            		eb.setFooter("Poll created by " + author.getAsTag(), author.getAvatarUrl());             		
            		API.sendMessage(log, eb.build());
        			        			
        		});    			
        		   			
    		}
    		    		
    	} 
    	
    	if (msg.getContentRaw().equalsIgnoreCase("s!ping")) {
    		
    		API.sendMessage(msgCh, "**Gateway**/**WebSocket**: **" + jda.getGatewayPing() + "**ms\n**REST**: **" + jda.getRestPing().complete() + "**ms", false);
    		
    	}
    	
    	if (msg.getContentRaw().equalsIgnoreCase("s!help")) {
    		
    		API.deleteMessage(msg);
    		API.sendPrivateMessage(author, jda.getEmoteById(541391545136447488L).getAsMention(), false);
    		
    	} 
    	
    	if (msg.getContentRaw().equalsIgnoreCase("s!uptime")) {
    		
    		final long duration = ManagementFactory.getRuntimeMXBean().getUptime();

            final long years = duration / 31104000000L;
            final long months = duration / 2592000000L % 12;
            final long days = duration / 86400000L % 30;
            final long hours = duration / 3600000L % 24;
            final long minutes = duration / 60000L % 60;
            final long seconds = duration / 1000L % 60;

            String uptime = (years == 0 ? "" : "**" + years + "**y, ") + (months == 0 ? "" : "**" + months + "**mo, ") + (days == 0 ? "" : "**" + days + "**d, ") + (hours == 0 ? "" : "**" + hours + "**h, ") + (minutes == 0 ? "" : "**" + minutes + "**m, ") + (seconds == 0 ? "" : "**" + seconds + "**s, ");

            uptime = API.replaceLast(uptime, ", ", "");
            uptime = API.replaceLast(uptime, ",", " and");

            API.sendMessage(msgCh, "Uptime: " + uptime + "", false);    		
    		
    	} 
    	
    	if (msg.getContentRaw().startsWith("s!eval")) {
    		
    		final String neededPerm = "ADMINISTRATOR";	
    		
    		if (!API.hasPerm(mem, Permission.valueOf(neededPerm))) {
    			
				API.sendMessage(msgCh, PermissionError.getErrorMessage(neededPerm), false);      			
    			
    		}  
    		
    		else {
    			
    			try {
    				
        			String toEval = msg.getContentRaw().substring(7);
        			EmbedBuilder eb = API.createEmbedBuilder(author);
        			engine.put("e", e);
        			engine.put("guild", guild);
        			engine.put("author", author);
        			engine.put("jda", jda);
        			engine.put("channel", e.getChannel());
        			eb.setTitle("CODE EVALUATION WAS SUCCESSFUL");
        			eb.addField("Result", "```java\n" + engine.eval(toEval) + "\n```", true);
        			eb.setColor(Color.WHITE);
        			API.sendMessage(e.getChannel(), eb.build());
    				
    			}
    			
    			catch (ScriptException ex) {
    				
    				ex.printStackTrace();
    				EmbedBuilder eb = API.createEmbedBuilder(author);
    				eb.setTitle("CODE EVALUATION WAS UNSUCCESSFUL");
    				eb.addField("Problem", ex.getMessage(), true);
    				eb.setColor(Color.RED);
    				API.sendMessage(e.getChannel(), eb.build());
    				
    			}    			
    			
    		}
    		
    	}    	
    	
    	if (msg.getContentRaw().startsWith("s!delete")) {
    		
    		final String neededPerm = "BAN_MEMBERS";	    		
    		
    		String[] args = msg.getContentRaw().split("\\s+");
    		int amount = Integer.parseInt(args[1]);  
    		int count;
    		API.deleteMessage(msg);
    		
    		if (!API.hasPerm(mem, Permission.valueOf(neededPerm))) {
    			
				API.sendMessage(msgCh, PermissionError.getErrorMessage(neededPerm), false);      			
    			
    		}
    		
    		else {
    			
        		if (msg.getMentionedUsers().isEmpty()) {
        			
        			amount++;
            		List<Message> messages = msgCh.getIterableHistory().cache(false).stream().filter(m -> !m.getAuthor().equals(jda.getSelfUser())).limit(amount).collect(Collectors.toList());        			
        			count = messages.size() - 1;        			
        			
        			if (messages.size() == 1) {
        				
        				messages.get(0).delete().queue();
        				msgCh.sendMessage(":white_check_mark: Deleted **1** message.").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));        				
        				
        			}
        			
        			else {
        				
        				if (messages.size() == 0) {
        					
        					API.sendMessage(msgCh, ":no_entry: There were no messages to delete.", false);        					
        					
        				}
        				
        				else {
        					
        					msgCh.purgeMessages(messages);
        					msgCh.sendMessage(":white_check_mark: Deleted **" + count + "** messages.").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));        					
        					
        				}
        				
        			}        			        		        		
        			
        		}        		          	
        		
        		else {        			
        			      			
            		List<Message> messagesByUser = msgCh.getIterableHistory().cache(false).stream().filter(m -> m.getAuthor().equals(msg.getMentionedUsers().get(0)) && !m.getAuthor().equals(jda.getSelfUser())).limit(amount).collect(Collectors.toList());    
            				
                	int uCount = messagesByUser.size();     
                	
        			if (messagesByUser.size() == 1) {
        				
        				messagesByUser.get(0).delete().queue();
        				msgCh.sendMessage(":white_check_mark: Deleted **1** message by user **" + msg.getMentionedUsers().get(0).getName() + "**.").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));        				
        				
        			}
        			
        			else {
        				
        				if (messagesByUser.size() == 0) {
        					
        					API.sendMessage(msgCh, ":no_entry: There were no messages to delete.", false);        					
        					
        				}
        				
        				else {
        					
        					msgCh.purgeMessages(messagesByUser);
        					msgCh.sendMessage(":white_check_mark: Deleted **" + uCount + "** messages by user **" + msg.getMentionedUsers().get(0).getName() + "**.").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));        					
        					
        				}
        				
        			}                	

        		}        		          	
    			
    		}    		
    		
    	}
    	
    	if (msg.getContentRaw().startsWith("s!user")) {
    		
    		if (msg.getMentionedUsers().isEmpty()) {
    			
    			EmbedBuilder eb = API.createEmbedBuilder(author);
    			
    			eb.setAuthor("USER INFO - " + author.getAsTag());
    			eb.setColor(Color.WHITE);
    			eb.setThumbnail(author.getAvatarUrl());    			
    			eb.addField("ID", "**" + author.getId() + "**", false);
    			
    			eb.addField("Nickname for this guild", "**" + (mem.getNickname() == null ? "None" : mem.getNickname()) + "**", false);
    			
            	cal.setTimeInMillis(author.getTimeCreated().toInstant().toEpochMilli());
        		String creatdate = date.format(cal.getTime()).toString();   
        		String creattime = time.format(cal.getTime()).toString(); 
        		
        		eb.addField("Account created", "**" + creatdate + "** | " + "**" + creattime + "** UTC", false);
        		
            	cal.setTimeInMillis(mem.getTimeJoined().toInstant().toEpochMilli());
        		String joindate = date.format(cal.getTime()).toString();   
        		String jointime = time.format(cal.getTime()).toString(); 
        		
        		eb.addField("User joined", "**" + joindate + "** | " + "**" + jointime + "** UTC", false); 
        		
        		if (mem.getRoles().size() == 0) {
        			
                	eb.addField("Roles [**0**]", "None", false);         			
        			
        		}
        		
        		else {
        		
                    int i = 0;
                	String s = "";
                		
                    for (Role role : mem.getRoles()) {
                        	
                         i++;
                            
                         if (i == mem.getRoles().size()) {
                            	
                             s += role.getName();
                                
                         }    
                            
                         else {
                            	
                             s += role.getName() + ", ";
                             
                          }    
                            
                    }  
                    
                    eb.addField("Roles [**" + i + "**]", s, false);                    
        			
        		}        		                	                	 
            	
            	API.sendMessage(msgCh, eb.build());
    			
    		}    		
    		
    		else {
    			
    			User user = msg.getMentionedUsers().get(0);
    			Member member = API.getMember(guild, user);
    			
    			EmbedBuilder eb = API.createEmbedBuilder(author);
    			
    			eb.setAuthor("USER INFO - " + user.getAsTag());
    			eb.setColor(Color.WHITE);    			
    			eb.setThumbnail(user.getAvatarUrl());
    			eb.addField("ID", "**" + user.getId() + "**", false);
    			
    			eb.addField("Nickname for this guild", "**" + (member.getNickname() == null ? "None" : member.getNickname()) + "**", false);
    			
            	cal.setTimeInMillis(user.getTimeCreated().toInstant().toEpochMilli());
        		String creatdate = date.format(cal.getTime()).toString();   
        		String creattime = time.format(cal.getTime()).toString(); 
        		
        		eb.addField("Account created", "**" + creatdate + "** | " + "**" + creattime + "** UTC", false);
        		
            	cal.setTimeInMillis(member.getTimeJoined().toInstant().toEpochMilli());
        		String joindate = date.format(cal.getTime()).toString();   
        		String jointime = time.format(cal.getTime()).toString(); 
        		
        		eb.addField("User joined", "**" + joindate + "** | " + "**" + jointime + "** UTC", false);         		
        			
        		if (member.getRoles().size() == 0) {
        			
                	eb.addField("Roles [**0**]", "None", false);         			
        			
        		}
        		
        		else {
        		
                    int i = 0;
                	String s = "";
                		
                    for (Role role : member.getRoles()) {
                        	
                         i++;
                            
                         if (i == member.getRoles().size()) {
                            	
                             s += role.getName();
                                
                         }    
                            
                         else {
                            	
                             s += role.getName() + ", ";
                             
                          }    
                            
                    }  
                    
                    eb.addField("Roles [**" + i + "**]", s, false);                    
        			
        		}                                           		
        		
            	API.sendMessage(msgCh, eb.build());            	
    	
    		}
    		
    	}
        
	}
	
	@Override
	public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent e) {
		
		Guild guild = e.getGuild();
		Role muted = guild.getRolesByName("Muted", false).get(0);
		
		if (e.getRoles().contains(muted)) {
			
			TextChannel log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("UNMUTE");
			eb.setColor(Color.GREEN);			
	        eb.setThumbnail((e.getUser().getAvatarUrl() == null ? e.getUser().getDefaultAvatarUrl() : e.getUser().getAvatarUrl()));						
			eb.addField("User", "**" + e.getUser().getAsTag() + "**", false);
			API.sendMessage(log, eb.build());
			
		}
		
	}
	
	@Override
    public void onGuildBan(GuildBanEvent e) {
		
		User user = e.getUser();
		Guild guild = e.getGuild();		
		TextChannel log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));
        
        Ban ban = guild.retrieveBan(user).complete();
        List<AuditLogEntry> auditbans = guild.retrieveAuditLogs().type(ActionType.BAN).complete();
        User banner = auditbans.get(0).getUser();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("NEW BAN");        
        eb.setThumbnail((user.getAvatarUrl() == null ? user.getDefaultAvatarUrl() : user.getAvatarUrl()));                
        eb.setColor(Color.RED);
        eb.addField("User", "**" + user.getAsTag() + "**", true);
        eb.addField("ID", "**" + user.getId() + "**", true);        
        eb.addField("Moderator", banner.getAsMention(), true);        
        eb.addField("Reason", "**" + (ban.getReason() == null ? "Unknown" : ban.getReason()) + "**", true);
               
		API.sendMessage(log, eb.build());
		
	}	
	
	@Override
    public void onGuildUnban(GuildUnbanEvent e) {
		
		User user = e.getUser();
		Guild guild = e.getGuild();		
		TextChannel log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("UNBAN");
        eb.setColor(Color.GREEN);        
        eb.setThumbnail((user.getAvatarUrl() == null ? user.getDefaultAvatarUrl() : user.getAvatarUrl()));                      
        eb.addField("User", "**" + user.getAsTag() + "**", true);
        eb.addField("ID", "**" + user.getId() + "**", true);        
		API.sendMessage(log, eb.build());
		
	}	
	
	@Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent e) {
		
		User user = e.getUser();
		Guild guild = e.getGuild();		
		TextChannel log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));	
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("USER HAS LEFT");        
        eb.setThumbnail((user.getAvatarUrl() == null ? user.getDefaultAvatarUrl() : user.getAvatarUrl()));                
        eb.setColor(Color.RED);
        eb.addField("User", "**" + user.getAsTag() + "**", true);
        eb.addField("ID", "**" + user.getId() + "**", true);
        API.sendMessage(log, eb.build());        	
		
	}	
	
	@Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
		
		User user = e.getUser();
		Guild guild = e.getGuild();		
		TextChannel log = guild.getTextChannelById(MySQL.getChannelId(guild.getIdLong()));	
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("USER HAS JOINED");        
        eb.setThumbnail((user.getAvatarUrl() == null ? user.getDefaultAvatarUrl() : user.getAvatarUrl()));                     
        eb.setColor(Color.GREEN);
        eb.addField("User", "**" + user.getAsTag() + "**", true);
        eb.addField("ID", "**" + user.getId() + "**", true);        
        API.sendMessage(log, eb.build());    
		
	}	
	
    @Override
    public void onGuildLeave(GuildLeaveEvent e) {
    	
		Guild guild = e.getGuild();    	
    	
    	if (MySQL.isInDatabase(guild.getIdLong())) {
    		
        	MySQL.removeData(guild.getIdLong());    		
    		
    	}    	
    	
    }
		
}

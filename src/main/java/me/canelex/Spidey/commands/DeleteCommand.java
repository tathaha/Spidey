package me.canelex.Spidey.commands;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import me.canelex.Spidey.objects.command.ICommand;
import me.canelex.Spidey.utils.API;
import me.canelex.Spidey.utils.PermissionError;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class DeleteCommand implements ICommand {

	@Override
	public final boolean called(final GuildMessageReceivedEvent e) {
		
		return true;
		
	}

	@Override
	public final void action(final GuildMessageReceivedEvent e) {
		
		final String neededPerm = "BAN_MEMBERS";	    		
		
		final String[] args = e.getMessage().getContentRaw().split("\\s+");
		int amount = Integer.parseInt(args[1]);  
		int count;
		API.deleteMessage(e.getMessage());
		
		if (!API.hasPerm(e.getMember(), Permission.valueOf(neededPerm))) {
			
			API.sendMessage(e.getChannel(), PermissionError.getErrorMessage(neededPerm), false);      			
			
		}
		
		else {
			
    		if (e.getMessage().getMentionedUsers().isEmpty()) {
    			
    			amount++;
    			final List<Message> messages = e.getChannel().getIterableHistory().cache(false).stream().limit(amount).collect(Collectors.toList());        			
    			count = messages.size() - 1;        			
    			
    			if (messages.size() == 1) {
    				
    				messages.get(0).delete().queue();
    				e.getChannel().sendMessage(":white_check_mark: Deleted **1** message.").queue(m -> {
    					
    					if (m != null) {
    						
    						m.delete().queueAfter(5, TimeUnit.SECONDS);
    						
    					}
    					
    				});        				
    				
    			}
    			
    			else {
    				
    				if (messages.size() == 0) {
    					
    					API.sendMessage(e.getChannel(), ":no_entry: There were no messages to delete.", false);        					
    					
    				}
    				
    				else {
    					
    					e.getChannel().purgeMessages(messages);
    					e.getChannel().sendMessage(":white_check_mark: Deleted **" + count + "** messages.").queue(m -> {
        					
        					if (m != null) {
        						
        						m.delete().queueAfter(5, TimeUnit.SECONDS);
        						
        					}
        					
        				});       					
    					
    				}
    				
    			}        			        		        		
    			
    		}        		          	
    		
    		else {        			
    			      			
    			final List<Message> messagesByUser = e.getChannel().getIterableHistory().cache(false).stream().filter(m -> m.getAuthor().equals(e.getMessage().getMentionedUsers().get(0))).limit(amount).collect(Collectors.toList());    
        				
    			final int uCount = messagesByUser.size();     
            	
    			if (messagesByUser.size() == 1) {
    				
    				messagesByUser.get(0).delete().queue();
    				e.getChannel().sendMessage(":white_check_mark: Deleted **1** message by user **" + e.getMessage().getMentionedUsers().get(0).getName() + "**.").queue(m -> {
    					
    					if (m != null) {
    						
    						m.delete().queueAfter(5, TimeUnit.SECONDS);
    						
    					}
    					
    				});        				
    				
    			}
    			
    			else {
    				
    				if (messagesByUser.size() == 0) {
    					
    					API.sendMessage(e.getChannel(), ":no_entry: There were no messages to delete.", false);        					
    					
    				}
    				
    				else {
    					
    					e.getChannel().purgeMessages(messagesByUser);
    					e.getChannel().sendMessage(":white_check_mark: Deleted **" + uCount + "** messages by user **" + e.getMessage().getMentionedUsers().get(0).getName() + "**.").queue(m -> {
        					
        					if (m != null) {
        						
        						m.delete().queueAfter(5, TimeUnit.SECONDS);
        						
        					}
        					
        				});        					
    					
    				}
    				
    			}                	

    		}        		          	
			
		}		
		
	}

	@Override
	public final String help() {
		
		return "Deletes messages (by mentioned user)";
		
	}

	@Override
	public final void executed(final boolean success, final GuildMessageReceivedEvent e) {
		
		return;
		
	}

}
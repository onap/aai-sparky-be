#!/usr/bin/perl

use strict;
use warnings;

my $filename = $ARGV[0];
my $outputfile= $ARGV[1];

open my $fh_input, '<', $filename or die "Cannot open $filename: $!";
open my $fh_output, '>', $outputfile or die "Cannot open $outputfile: $!";

while ( my $line = <$fh_input> ) {
    chomp ($line);

    if ( $line =~ /(.*)(\".*\")(.*)/ ) {

       # we have seen examples of the status field containing quoted comma-delimited
       # strings which is messing up parsing of the record data which is supposed to be
       # comma-separated at the field level.  This little block converts sections of
       # this type of data into a single-quoted-string with a semi-colon delimiter instead.

       my $beforeBadStr = $1;
       my $badStr       = $2;
       my $afterBadStr  = $3;

       $badStr =~ s/,/;/g;
       $badStr =~ s/"/'/g;

       $line = $beforeBadStr . $badStr . $afterBadStr ;

    }

    my @row = split(",", $line);
    print $fh_output "{\"index\":{\"_index\":\"topographicalsearchindex-localhost\",\"_type\":\"default\"}\n";
    print $fh_output "{\"pkey\": \"$row[0]\", \"entityType\": \"$row[1]\", \"location\" : {\"lat\": \"$row[3]\", \"lon\": \"$row[2]\"}, \"selfLink\": \"$row[4]\"}\n";

}

close($fh_input);
close($fh_output);


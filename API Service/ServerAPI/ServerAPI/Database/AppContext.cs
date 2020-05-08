using System;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Metadata;
using ServerAPI.Models;

namespace ServerAPI.Database
{
    public partial class AppContext : DbContext
    {
        public AppContext()
        {
        }

        public AppContext(DbContextOptions<AppContext> options)
            : base(options)
        {
        }

        public virtual DbSet<Building> Building { get; set; }
        public virtual DbSet<Company> Company { get; set; }
        public virtual DbSet<Floor> Floor { get; set; }
        public virtual DbSet<Location> Location { get; set; }
        public virtual DbSet<Neighbor> Neighbor { get; set; }
        public virtual DbSet<Room> Room { get; set; }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            if (!optionsBuilder.IsConfigured)
            {
#warning To protect potentially sensitive information in your connection string, you should move it out of source code. See http://go.microsoft.com/fwlink/?LinkId=723263 for guidance on storing connection strings.
                optionsBuilder.UseSqlServer("Server=13.229.117.90,1433;Initial Catalog=hieu;Persist Security Info=False;User ID=hieu;Password=lenhhoXung21@@@;");
            }
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Building>(entity =>
            {
                entity.Property(e => e.Id)
                    .HasMaxLength(250)
                    .IsUnicode(false);

                entity.Property(e => e.CompanyId)
                    .IsRequired()
                    .HasMaxLength(250)
                    .IsUnicode(false);

                entity.Property(e => e.DayExpired).HasColumnType("datetime");

                entity.Property(e => e.Description)
                    .IsRequired()
                    .HasMaxLength(250);

                entity.Property(e => e.Name)
                    .IsRequired()
                    .HasMaxLength(250);

                entity.HasOne(d => d.Company)
                    .WithMany(p => p.Building)
                    .HasForeignKey(d => d.CompanyId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("FK__Building__Compan__060DEAE8");
            });

            modelBuilder.Entity<Company>(entity =>
            {
                entity.Property(e => e.Id)
                    .HasMaxLength(250)
                    .IsUnicode(false);

                entity.Property(e => e.Name)
                    .IsRequired()
                    .HasMaxLength(250);
            });

            modelBuilder.Entity<Floor>(entity =>
            {
                entity.Property(e => e.Id)
                    .HasMaxLength(250)
                    .IsUnicode(false);

                entity.Property(e => e.BuildingId)
                    .IsRequired()
                    .HasMaxLength(250)
                    .IsUnicode(false);

                entity.Property(e => e.LinkMap)
                    .IsRequired()
                    .HasMaxLength(250)
                    .IsUnicode(false);

                entity.Property(e => e.Name)
                    .IsRequired()
                    .HasMaxLength(250);

                entity.HasOne(d => d.Building)
                    .WithMany(p => p.Floor)
                    .HasForeignKey(d => d.BuildingId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("FK__Floor__BuildingI__47DBAE45");
            });

            modelBuilder.Entity<Location>(entity =>
            {
                entity.Property(e => e.Id)
                    .HasMaxLength(250)
                    .IsUnicode(false);

                entity.Property(e => e.FloorId)
                    .IsRequired()
                    .HasMaxLength(250)
                    .IsUnicode(false);

                entity.Property(e => e.LinkQrcode)
                    .IsRequired()
                    .HasColumnName("LinkQRCode")
                    .HasMaxLength(250)
                    .IsUnicode(false);

                entity.Property(e => e.Name)
                    .IsRequired()
                    .HasMaxLength(250);

                entity.Property(e => e.QranchorId)
                    .HasColumnName("QRAnchorId")
                    .HasMaxLength(250)
                    .IsUnicode(false);

                entity.Property(e => e.SpaceAnchorId)
                    .HasMaxLength(250)
                    .IsUnicode(false);

                entity.HasOne(d => d.Floor)
                    .WithMany(p => p.Location)
                    .HasForeignKey(d => d.FloorId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("FK__Location__FloorI__1367E606");
            });

            modelBuilder.Entity<Neighbor>(entity =>
            {
                entity.Property(e => e.LocationBesideId)
                    .IsRequired()
                    .HasMaxLength(250)
                    .IsUnicode(false);

                entity.Property(e => e.LocationId)
                    .IsRequired()
                    .HasMaxLength(250)
                    .IsUnicode(false);

                entity.Property(e => e.Orientation)
                    .IsRequired()
                    .HasMaxLength(50);

                entity.HasOne(d => d.LocationBeside)
                    .WithMany(p => p.NeighborLocationBeside)
                    .HasForeignKey(d => d.LocationBesideId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("FK__LocationB__Locat__1920BF5C");

                entity.HasOne(d => d.Location)
                    .WithMany(p => p.NeighborLocation)
                    .HasForeignKey(d => d.LocationId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("FK__LocationB__Locat__182C9B23");
            });

            modelBuilder.Entity<Room>(entity =>
            {
                entity.Property(e => e.Id)
                    .HasMaxLength(250)
                    .IsUnicode(false);

                entity.Property(e => e.LocationId)
                    .IsRequired()
                    .HasMaxLength(250)
                    .IsUnicode(false);

                entity.Property(e => e.Name)
                    .IsRequired()
                    .HasMaxLength(50);

                entity.Property(e => e.SpaceAnchorId).HasMaxLength(250);

                entity.HasOne(d => d.Location)
                    .WithMany(p => p.Room)
                    .HasForeignKey(d => d.LocationId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("FK__Room__LocationId__35BCFE0A");
            });

            OnModelCreatingPartial(modelBuilder);
        }

        partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
    }
}
